# Simple mics assets

Authoritative references: mics/simple-home-tasks-shop.png, mics/simple-garden-care-journal.png, mics/simple-task-seed-planting.png.

Built-in imagegen was used with these three local paths as visual inputs. These are reference-inspired isolated sprites/scenery, not lossless crops. Every asset is saved in core-ui/src/main/res/drawable-nodpi; no complete screen is used as UI background. Native Compose surfaces and SimpleGardenArt draw the simple material, buttons, journal and icons.

## Native scene and botanical details

SimpleSceneDetails.kt draws the small cottage and hanging lantern inspired by screens01/03, and the book handles inspired by screen09, using native Compose Canvas paths/shapes. GardenSprig.kt draws the olive sprigs inspired by the journal, task and seed-detail accents. Shelf fronts use native gradient/border shapes. These are authored code illustrations, with no generator prompt, external image or baked text. They are separate from the background assets and live controls.

## simple_home.png

Use case: reference-inspired scenery asset. Use first input sheet screen01 Khu vườn as authoritative simple flat painted style. Extract/reconstruct ONLY the forest landscape, no UI. Portrait 2:3. Flat teal layered woodland silhouettes with broad green leaves/branches framing top corners, one small waterfall on far left flowing into stream in lower third. One wide central mossy grey rock island around 55%height to stand a tree on; grassy ground across bottom quarter for interactive signs and care shelf. Remove cherry tree, all titles, plaques, labels, signs, baskets, tools, navigation and lantern. Smooth muted soft 2D shapes exactly like reference, no detailed bark or realistic leaves. No text, no plants being grown, no photoreal textures. Deliver just an empty landscape background, never complete UI screen.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-6a0425e2-f6f6-4828-935a-648c3741c875.png

## simple_shop_v2.png

Use case: reference-inspired scenery. Input first reference sheet screen03. Only a SIMPLE EMPTY brown tree-trunk SHOP ALCOVE and red-orange cream-spotted mushroom awning. Portrait2:3. Broad smooth flat shaded shapes matching mics exactly. Put red mushroom canopy ONLY in upper quarter, opening beneath dark warm brown, untextured. Green leaves in corners. IMPORTANT: no shelves at all, no horizontal lines below awning, no plants, no fertilizer, no lantern, no title, no plaques, no UI, no text, no footer. Shelves and objects are separate live components in app. The dark alcove below awning is completely empty. Not a UI screenshot.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-853ce5a8-6bfc-4496-b44a-6077d819d23b.png

## simple_seed.png

Use case: background-extraction. Isolate only the sprouting almond-shaped orange seed and earth mound from screen09 in third input sheet (simple-task-seed-planting). EXACT simple soft flat painted style of that source. Orange almond seed tilted right on dark brown low mound of soil with a few round light-brown grey pebbles. Short thin green stem from pointed top and two fresh green leaves. Transparent background. No grass, forest, label, text, frame, UI or realistic detail. Keep seed long pointed oval almond, not round orange fruit. Frame tightly with only small transparent margin.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-3a556926-b8f0-43bc-b567-88252b9354be.png

## simple_cherry.png

Extract the pink flowering cherry tree from screen05 center in second reference. Single whole twisting trunk, broad soft pink blossom canopy, low moss island with grey rocks, few small pink flowers. Exactly the simple flat soft painted shapes from reference, no photographic grain or detailed bark. Transparent cutout, no text, no panel, no forest.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-a21f439a-5bf0-40c6-864d-83e56b2b2585.png

## simple_pine.png

Extract the small pine bonsai from screen04 second reference. Single pine with curved brown trunk and three olive green needle cushion canopies, low moss island and grey stones. Exactly simple soft painted flat shapes. Transparent cutout, no text, no panel, no forest, no photoreal texture.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-c9346c4b-6d8c-48bb-be18-9f20559f2d1d.png

## simple_succulent.png

Extract the jade succulent from screen08 in third reference. Large mint green rosette leaves with pale yellow edges and soft pink tips, short mound of brown earth and grey stones. Simple soft flat illustration matching reference exactly. Transparent cutout, no pot, no text, no frame.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-6ce32421-32c1-4b43-b34e-5cfa44c51791.png

## simple_mandarin.png

Extract potted mandarin from screen03 first reference, four round orange fruit, oval green leaves and thin brown trunk in small beige pot. Simple flat soft painted shapes, no tiny detail. Transparent cutout no text no UI.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-827219bd-e231-47c7-8e41-ab1b248bb271.png

## simple_forest.png

Create a scenery-only background from screen04 in second reference. Portrait2:3 crop. Preserve the simple flat layered teal forest, leafy branches at upper corners, waterfall on left, teal stream filling bottom, low green bushes and rounded grey rocks. Empty central clearing and several blank moss islands. REMOVE ALL titles, labels, plants, trees being grown, baskets, UI buttons, navigation, lantern. Not an app screenshot. Only simplified forest environment, no photoreal detail.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-4dd00952-0656-4e7b-b366-6b26a6239209.png

## simple_shop.png

Extract scenery only from screen03 first reference: portrait2:3 smooth brown tree trunk shop alcove with soft orange red spotted mushroom canopy, cream spots, branches/green leaves corners. Three empty honey brown shelves across interior at roughly40%,62%,82%height. REMOVE ALL plants, bags, panels, titles, coins, footer and nav, all text. Empty shelves, simple flat softly shaded illustration from source, no photoreal texture.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-3c1579e5-5374-4021-b37c-a6d91d19534f.png

## simple_tools.png

Single blue watering can from screen05 second reference, white five petal flower yellow center, curved handle, spout pointing upper left. Simple flat softly painted cartoon exactly source style, no photoreal detailed metal. Transparent cutout only, no shelf or text.

Generated images are saved to C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de as C:\Users\AP\.codex\generated_images\01a1b68d-8de0-7cb2-a083-432f68eaa9de\exec-6059023f-a44e-40da-bbf6-b314a5b7e2dd.png
