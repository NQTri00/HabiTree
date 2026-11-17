"""Locate Kotlin string literals without confusing comments or template expressions.

Offsets refer to the original source; consumers apply replacements right-to-left.
Template argument expressions remain Kotlin code and are never translated.
"""
from dataclasses import dataclass
import re

@dataclass
class Literal:
    start: int
    end: int
    parts: list[str]
    arguments: list[str]
    raw: bool = False

    @property
    def template(self):
        return ''.join(part + (f'{{{i}}}' if i < len(self.arguments) else '') for i, part in enumerate(self.parts))

def _comment_end(source, i):
    if source.startswith('//', i):
        end=source.find('\n', i+2)
        return len(source) if end < 0 else end
    depth=1; j=i+2
    while j < len(source) and depth:
        if source.startswith('/*',j):depth+=1;j+=2
        elif source.startswith('*/',j):depth-=1;j+=2
        else:j+=1
    return j

def _char_end(source, i):
    j=i+1
    while j<len(source):
        if source[j]=='\\':j+=2
        elif source[j]=="'":return j+1
        else:j+=1
    return j

def _expression_end(source, i):
    depth=1;j=i
    while j<len(source):
        if source.startswith(('//','/*'),j):j=_comment_end(source,j)
        elif source[j]=='"':j=_literal(source,j).end
        elif source[j]=="'":j=_char_end(source,j)
        elif source[j]=='{':depth+=1;j+=1
        elif source[j]=='}':
            depth-=1
            if depth==0:return j
            j+=1
        else:j+=1
    raise ValueError(f'Unclosed Kotlin template at {i}')

def _literal(source, start):
    raw=source.startswith('"""',start);quote='"""' if raw else '"';j=start+len(quote)
    parts=[''];arguments=[]
    escapes={'n':'\n','r':'\r','t':'\t','b':'\b','"':'"',"'":"'",'\\':'\\','$':'$'}
    while j<len(source):
        if source.startswith(quote,j):return Literal(start,j+len(quote),parts,arguments,raw)
        if not raw and source[j]=='\\':
            if source[j+1]=='u':parts[-1]+=chr(int(source[j+2:j+6],16));j+=6
            else:parts[-1]+=escapes.get(source[j+1],source[j:j+2]);j+=2
        elif source.startswith('${',j):
            end=_expression_end(source,j+2);arguments.append(source[j+2:end]);parts.append('');j=end+1
        elif source[j]=='$' and (match:=re.match(r'[A-Za-z_][A-Za-z_0-9]*',source[j+1:])):
            arguments.append(match.group());parts.append('');j+=len(match.group())+1
        else:parts[-1]+=source[j];j+=1
    raise ValueError(f'Unclosed Kotlin string at {start}')

def literals(source):
    result=[];i=0
    while i<len(source):
        if source.startswith(('//','/*'),i):i=_comment_end(source,i)
        elif source[i]=="'":i=_char_end(source,i)
        elif source[i]=='"':
            value=_literal(source,i);result.append(value);i=value.end
        else:i+=1
    return result

VIETNAMESE=re.compile(r'[ĂÂĐÊÔƠƯăâđêôơưÀÁẢÃẠẰẮẲẴẶẦẤẨẪẬÈÉẺẼẸỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌỒỐỔỖỘỜỚỞỠỢÙÚỦŨỤỪỨỬỮỰỲÝỶỸỴàáảãạằắẳẵặầấẩẫậèéẻẽẹềếểễệìíỉĩịòóỏõọồốổỗộờớởỡợùúủũụừứửữựỳýỷỹỵ]')

def has_copy(value):
    text=''.join(value.parts)
    if re.match(r'\s*(SELECT|CREATE|INSERT|UPDATE|DELETE|PRAGMA|ALTER|DROP|WITH)\b',text):return False
    return bool(VIETNAMESE.search(text) or re.search(r'(?:^|[ .·+])xu(?:$|[/ .·])',text) or text in {'Mua','Mua & gieo','Mua & gieo 1'})
