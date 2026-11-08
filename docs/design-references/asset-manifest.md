# Reference-driven garden artwork

CURRENT ACCEPTANCE: 90 percent similarity, every illustrated component extracted from the user-provided designs. Existing generated assets listed below are provisional reconstructions, NOT verified literal extractions. Preserve these generation records for provenance, but replace their consumers with source-derived artwork before final acceptance. Tree-growth animation is deferred; use static source trees.

Source: user-attached design sheets for screens 01–09 and seed-growth animation, received 2026-11-08.

The artwork must be used as separate reusable components, with live Compose text and controls. Complete reference screen images must never be used as UI backgrounds.

## Implemented asset

- Resource: core-ui/src/main/res/drawable-nodpi/botanical_parchment_panel.png
- Consumer: GardenBotanicalFrame, using nine image patches to preserve corners.
- Tool: built-in imagegen with the attached references.
- Prompt: Extract/reconstruct one isolated blank warm ivory parchment panel matching modal screens 07 and 08, with aged curled paper edges, braided painted wood/root border, green vines and small pink cherry blossoms. Keep at least 75 percent blank usable center; transparent outside; no lettering, icons, buttons, inputs, scene or full-screen mockup. Faithfully preserve the detailed painted storybook materials.

## Navigation tile and typography

- Resource: core-ui/src/main/res/drawable-nodpi/garden_navigation_tile.png
- Consumer: GardenTrailBar, one independent selectable tile per destination, with live labels and a drawn selection indicator.
- Tool: built-in imagegen using the attached reference sheets.
- Prompt: Extract/reconstruct one isolated dark petrol-teal carved organic wood navigation plaque with worn blue-green grain, slender antique-gold edges, vines and moss, transparent outside, and a blank center for separately rendered icons and text. Match the detailed painted storybook references; no screen, scene, lettering or icons.
- GardenTypography applies serif type to the runtime theme and the preview theme.
- Verification: :app:assembleDebug passed; GardenTrailBarPreview and TasksScreenPopulatedPreview rendered successfully.

## Nursery, shop and painted controls — 2026-11-08

Tool for all assets below: built-in imagegen, transparent background, generated as independent component artwork. Copies saved in core-ui/src/main/res/drawable-nodpi; original files retained under C:/Users/AP/.codex/generated_images/01a1b68d-8de0-7cb2-a083-432f68eaa9de. No full reference screenshot is used as a screen background.

- nursery_pine.png — exec-dddb34e3-b869-4eb5-a123-803ffb8240cd.png
  Prompt: A miniature graceful Japanese pine bonsai growing directly from a few mossy rocks, no pot, isolated transparent background. Rich detailed hand-painted storybook fantasy garden illustration, curved warm brown bark, layered dense emerald needle fans, tiny golden sun highlights, three-quarter view. Entire tree visible. No text, UI, border, scenery, shadow rectangle. Asset for woodland garden Android game.
- nursery_succulent.png — exec-5f6b9689-30c0-4a13-a904-4076017a6ba2.png
  Prompt: A jade rosette succulent with thick mint emerald leaves and delicate rose pink tips growing directly from small mossy stones, no pot, isolated transparent background. Rich detailed hand-painted storybook enchanted garden art, three-quarter view, warm golden highlights. Entire plant visible. No text, UI, border or scene.
- nursery_seed_basket.png — exec-bcf83b72-1d55-429b-af8b-977d8804c222.png
  Prompt: A wide shallow woven wicker garden basket, three-quarter front view, containing two small blank cream paper seed packets tied with twine, packets have a small pink cherry flower and pine sprig illustration respectively. Rich hand-painted storybook enchanted woodland game art, warm brown wicker, green leaves and a few pink blossoms around outer basket. Basket width twice height, transparent background. No words, text, lettering, UI, scene or backdrop.
- nursery_shelf.png — exec-5b675c48-358c-4229-9ba3-3b2509a3d36a.png
  Prompt: Use case: illustration-story. Isolated transparent game UI component: one wide thick rustic wooden nursery shelf plank, front three-quarter view, warm honey brown bark and grain, rounded carved edges, tiny moss, green ivy and a couple pink blossoms only at ends, shelf is 5 times wider than high. Rich detailed hand-painted enchanted woodland storybook art. No objects on shelf, no lettering, no background, no UI screenshot.
- garden_wood_sign.png — exec-1db014cf-c4c7-4d06-a4a2-8d7a013c9502.png
  Prompt: Use case: illustration-story. One isolated wide wooden signboard with blank dark warm brown center, curved natural bark edges, green vines wrapping corners and a few pink cherry blossoms, golden carved trim. Rich detailed hand-painted enchanted forest storybook game art. Aspect about 3.5:1. Blank center for live title text, transparent outside. No lettering, no full scene, no icons.
- garden_leaf_button.png — exec-b1a30a7d-c20d-4b52-a914-0586b5f45ed7.png
  Prompt: Use case: illustration-story. One isolated wide rounded game button made of a deep olive-green leaf plaque encircled with braided thin golden vine, tiny fresh green leaves at edges, highlights on upper rim, hand-painted rich whimsical woodland storybook art. Center blank flat dark green for separate white text. Aspect 3:1, transparent outside, no lettering, no icons or scene.
- garden_fertilizer_sack.png — exec-ec89f0e6-a1db-43e2-8d7a-c44035e2c87e.png
  Prompt: Use case: illustration-story. One isolated small cream burlap sack of organic fertilizer tied at top with warm brown rope, green two-leaf sprout emblem painted on front. Rich detailed hand-painted enchanted woodland storybook art, warm golden light, full object visible centered, transparent background. No words, lettering, UI, scene.
- nursery_shop_frame.png — exec-767b9a12-0944-4bc9-9824-ef5f8d652d6b.png
  Prompt: Use case illustration-story. Reusable isolated wooden nursery shop frame asset for a fantasy garden game: tall open-front hollow tree trunk cabinet with a broad red mushroom canopy roof, stout gnarled wooden side posts, tiny warm hanging lantern at upper right, green vines and pink blossoms. Front view, tall portrait frame. Wide open EMPTY transparent center for separately placed shelves and products. No built-in shelf planks across center, no products, no signs, no lettering, no text, no interface, no complete background or scenery. Rich detailed hand-painted storybook art, warm golden wood grain and deep mossy shadows, delicate emerald foliage.

Consumers: PaintedGardenPlant, GardenScreen, ShopScreen, GardenPaintedControls, AddTaskDialog. Shared nursery_painted_seed/trunk/blossoms/leaves are copies of previously generated tree layers, not new generations.

## Other integrated components

- forest_arch/stream/treehouse/seed_stall/island are separately generated transparent storybook woodland objects. GardenForestScene layers border/water sprites around live UI.
- garden_journal_page_clean is the active bound-paper asset with upper foliage removed to protect live headings. Original garden_journal_page retained.
- Tree uses independent soil/seed/bark/leaf/blossom layers with runtime movement; these were reconstructed, not literal GIF crops.

## Remaining work

Painted navigation/care/task icons, species-correct plants, tree accessories, further scene refinement and full-screen reference review remain. See progress.md for current verification and acceptance gaps.
