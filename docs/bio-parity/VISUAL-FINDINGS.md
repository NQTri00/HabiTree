# Visual findings from original rendered images

This is an unfinished review. Each finding below comes from viewing actual Studio Compose previews beside the approved phone images, not from a code checklist. The target images remain unchanged.

## Reviewed so far

- Home: horizontal inset and task clipping corrected. Hero is now about 2 px too tall; reduce the space between artwork and title. Tree drop shadow is still weaker than the reference.
- Tasks: card heights and completed-task pink corrected. Filter strip lacks its top/bottom breathing room, so the first task still starts about 8 px too high. Summary tag/row is slightly shorter. Card shadows remain weaker.
- Shop: correct two-column layout and independent artwork. Border padding and tab height corrected in current pass; fresh image still needs inspection.
- Tree: oversized artwork reduced from 220 to 208 dp; compact watering button widened. Fresh image still needs inspection. Arrow containers need comparison to reference.
- Garden: plot number incorrectly occupies a separate row; reference overlays it inside the artwork. Artwork therefore starts too low, while plot titles have too little separation. Empty plot also needs the dashed art border. Filter strip shares Tasks spacing issue.
- Journal: main structure matches. First metrics are a few pixels short; the pink summary card has insufficient inner padding. Chart bars and labels need their exact reference spacing.

## Pending

Other routes, tabs, onboarding steps, popups and state variants are not yet visually signed off. No claim of 100% parity is justified.

## Additional visual findings (current 63-image pass)

- Task detail: facts text is too large (12 vs 11), making the facts card taller. Reward pill should align vertically with intro, not at its top.
- Task form: first field needs the form's top spacing; subsequent field gap must be 16 rather than 18. Native select arrow differs. Detail accordion/reward positions need another render.
- Inventory: very close geometry; plant downsampling/font rasterization and shadows still differ.
- Tree: artwork now matches size. Hero remains a couple pixels tall; arrows need negative horizontal inset and exact reference dimensions. Care button width now close.
- Focus: remove vertical ring padding; ring box should be 240, stroke inside its bounds. Current outer ring is ~268 and shifts the entire form downward. Preset 25 must not be preselected in the idle reference. Mono font differs visibly from Windows web fallback.
- Templates: missing soft icon background and tinted card border; rows about 10 px too short. Add internal text spacing and restore optional time interval in last line. Checkbox 20, not 18.
- Review/Insights/Achievements/Timeline/Settings/Encyclopedia: raw large headings use excessive line height and shift content down ~7 px. Use reference heading line height.
- Rest: calendar legend wraps but reference fits one line at smaller type; REST/PROTECTED colors are reversed. Native date field currently ISO text vs formatted reference. Date picker behavior required.
- Achievements: medal 48 rather than 44; body/progress row spacing needs adjustment after heading fix.
- Timeline: incorrect full rectangular border. Replace with only a left rule and milestone dots; use 12 px item titles. Multiline placeholders incorrectly centered vertically: align all multiline input text to top.
- Settings: switches need browser-style 3 px end inset; divided toggle rows currently add border outside reference row height.
- Data: remove extra 18 px spacer before nested privacy accordion. Bottom accordion spacing needs separate comparison.
- Encyclopedia: unknown plant placeholder should use solid soft color. Compact description line height needed; cards currently too tall.
- Species: stage previews lack soft background, 6 px inset and 52 px art height. Use equal stage card dimensions for known and locked; current art oversized and lower rows clip. Hero art/text vertical rhythm differs slightly.
- Onboarding step 0: native uses uniform 18 px gap inside what is a single web content group, plus unnecessary spacer. Group art(220), title(29,39.15), description(margin12) into one column to move footer up ~60 px.

- More/Insights links: native link cards are 72 vs reference 76 high; reduce gap between repeated links from 18 to 12 where the web groups them in a stack.
- Shop/Inventory care/accessory: icon placeholders are solid soft pink in reference, not gradients. Tree decor tab shares this issue. Tree info shares fact typography issue.
- Paused/archived tasks: correct content/actions, but empty panel needs dashed border and reference height; current panel too compact and subsequent link too high.
- Habit detail 7/30: calendar palette/legend issues recur; body and reminder section shifts accumulate. Time text formatting differs (09:00 vs browser 09:00 AM).
- First nine popups reviewed as paired image contacts: containment and overall hierarchy hold. Close button reference focus border is missing. Quantity minus renders as '?' because the shell-to-Python edit lost its Unicode character; replace with Kotlin ASCII unicode escape and re-render. Permanent-item icon/row dimensions need refinement. Purchase bottom/button spacing differs slightly; modal border consumption should be included in padding. Accessory/plant/water-all broadly align but inherit field/facts spacing.

## Third iteration observed (fresh compiled images)

Home now closely aligns: hero ends at583, task begins651, matching target geometry. Tasks first card407 vs target409: summary tag border contributes missing2px. Garden overlay numbers/dashed empty art corrected; final care line/card still a few px short. Native shadows now drawn from actual card paths and look closer.

Task form first field now134, matching target; later fields drift2px. Details header label is too high; reward within~2px. Task facts still6px too tall because native single-line row rounding; use a minimum39px row with10px vertical inset. Templates first card172 vs target164 because BioIntro adds6px paragraph margin but reference plain heading group does not; increase row padding by1 for90px cards and include saved time interval in caption.

Focus ring now240 and correct overall placement. Native mono text remains wider: reduce letter spacing by font size*0.05 to match Consolas character advance (46px timer needs extra-2.3px); slash-zero glyph still differs. This is also why date pill width55 vs reference52.

Timeline rule/dots and top-aligned multiline placeholders fixed. Date/title align; link and textarea still8px too low: remove repeated4px gaps except date-to-title, reduce pre-save spacer12to8, compensate inter-item spacing24to28.

Species stage tiles correct. Hero ~8px short: tag border2 + art216to220 +2px above art should restore8. No-action BioSection should26 rather than32; this then puts stage grid496 like reference. Locked stage tiles need whole-tile opacity.45.

Onboarding step0 now matches major geometry: art/title/footer within1-2px. Remaining steps and fresh popups not yet inspected in this iteration.

Further third-pass corrections to apply after batch: BioTag border consumes2px (padding9/6 vs8/5); quantity row minimum52 and number text centered. This should correct purchase modal height/centering (currently ~5px short). Reference quantity and values use centered text; minus now renders correctly. Mono letter-spacing adjustment size*-.05 pending. More/Insights/Stats grouped links need12px gaps, not18. Achievements criterion should10px, progress value12px and spacer6 vs8 to fix cumulative row drift. Species +8px hero and no-action section26 fixes queued. Secondary buttons should white at80% alpha per reference. Field time/date formatting and real picker interactions remain outstanding.
