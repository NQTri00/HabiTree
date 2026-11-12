package com.mamxanh.shared.data.sql

/** Portable SHA-256 for accidental-corruption detection, not encryption or authentication. */
@OptIn(ExperimentalUnsignedTypes::class)
object BackupDigest {
    fun sha256(bytes: ByteArray): String {
        val k = uintArrayOf(
            0x428a2f98u,0x71374491u,0xb5c0fbcfu,0xe9b5dba5u,0x3956c25bu,0x59f111f1u,0x923f82a4u,0xab1c5ed5u,
            0xd807aa98u,0x12835b01u,0x243185beu,0x550c7dc3u,0x72be5d74u,0x80deb1feu,0x9bdc06a7u,0xc19bf174u,
            0xe49b69c1u,0xefbe4786u,0x0fc19dc6u,0x240ca1ccu,0x2de92c6fu,0x4a7484aau,0x5cb0a9dcu,0x76f988dau,
            0x983e5152u,0xa831c66du,0xb00327c8u,0xbf597fc7u,0xc6e00bf3u,0xd5a79147u,0x06ca6351u,0x14292967u,
            0x27b70a85u,0x2e1b2138u,0x4d2c6dfcu,0x53380d13u,0x650a7354u,0x766a0abbu,0x81c2c92eu,0x92722c85u,
            0xa2bfe8a1u,0xa81a664bu,0xc24b8b70u,0xc76c51a3u,0xd192e819u,0xd6990624u,0xf40e3585u,0x106aa070u,
            0x19a4c116u,0x1e376c08u,0x2748774cu,0x34b0bcb5u,0x391c0cb3u,0x4ed8aa4au,0x5b9cca4fu,0x682e6ff3u,
            0x748f82eeu,0x78a5636fu,0x84c87814u,0x8cc70208u,0x90befffau,0xa4506cebu,0xbef9a3f7u,0xc67178f2u).map { it.toInt() }
        val h = intArrayOf(0x6a09e667,0xbb67ae85u.toInt(),0x3c6ef372,0xa54ff53au.toInt(),0x510e527f,0x9b05688cu.toInt(),0x1f83d9ab,0x5be0cd19)
        val data = ByteArray(((bytes.size + 9 + 63) / 64) * 64)
        bytes.copyInto(data); data[bytes.size] = 0x80.toByte()
        val bits=bytes.size.toLong()*8
        repeat(8){data[data.lastIndex-it]=(bits ushr (it*8)).toByte()}
        fun r(v:Int,n:Int)=(v ushr n) or (v shl (32-n))
        val w=IntArray(64)
        for(offset in data.indices step 64) {
            repeat(16){i->w[i]=(0..3).fold(0){v,j->(v shl 8) or (data[offset+i*4+j].toInt() and 255)}}
            for(i in 16..63){val a=w[i-15];val b=w[i-2];w[i]=w[i-16]+(r(a,7) xor r(a,18) xor (a ushr 3))+w[i-7]+(r(b,17) xor r(b,19) xor (b ushr 10))}
            var a=h[0];var b=h[1];var c=h[2];var d=h[3];var e=h[4];var f=h[5];var g=h[6];var z=h[7]
            repeat(64){i->
                val t=z+(r(e,6) xor r(e,11) xor r(e,25))+((e and f) xor (e.inv() and g))+k[i]+w[i]
                val u=(r(a,2) xor r(a,13) xor r(a,22))+((a and b) xor (a and c) xor (b and c))
                z=g;g=f;f=e;e=d+t;d=c;c=b;b=a;a=t+u
            }
            intArrayOf(a,b,c,d,e,f,g,z).forEachIndexed{i,v->h[i]+=v}
        }
        return h.joinToString(""){it.toUInt().toString(16).padStart(8,'0')}
    }
}
