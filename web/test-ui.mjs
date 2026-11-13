import assert from "node:assert/strict";
import { readFileSync, existsSync } from "node:fs";
import { Script, createContext } from "node:vm";

const html = readFileSync(new URL("./index.html", import.meta.url), "utf8");
const source = html.match(/<script>([\s\S]*?)<\/script>/)?.[1];
assert.ok(source, "inline app script is present");
const script = new Script(source, { filename: "web/index.html inline JS" });

const listeners = new Map();
const nodes = new Map();
let hash = "#/home";
function node(id) {
  if (!nodes.has(id)) {
    nodes.set(id, {
      innerHTML: "",
      scrollTop: 0,
      classList: { add() {}, remove() {}, toggle() {} },
      addEventListener(type, handler) { listeners.set(id + ":" + type, handler); },
      querySelector() { return { focus() {} }; }
    });
  }
  return nodes.get(id);
}
const storage = new Map();
const document = {
  getElementById: node,
  querySelector() { return { classList: { toggle() {} } }; },
  addEventListener(type, handler) { listeners.set(type, handler); }
};
const localStorage = {
  getItem(key) { return storage.get(key) ?? null; },
  setItem(key, value) { storage.set(key, value); }
};
const context = createContext({
  document,
  window: { addEventListener() {} },
  localStorage,
  location: { get hash() { return hash; } },
  history: { pushState(_state, _title, newHash) { hash = newHash; } },
  setTimeout() { return 1; }, clearTimeout() {},
  setInterval() { return 1; }, clearInterval() {},
  console,
  Date,
  Intl
});
script.runInContext(context);
function click(attrName, value) {
  const button = {
    dataset: { [attrName.replace(/^data-/, "").replace(/-([a-z])/g, (_, c) => c.toUpperCase())]: value },
    hasAttribute(name) { return name === attrName; },
    getAttribute(name) { return name === attrName ? value : null; }
  };
  listeners.get("click")({ target: { closest() { return button; } } });
}
function view() { return node("view").innerHTML; }
assert.match(view(), /Mỗi ngày một chút/, "Home renders");
click("data-route", "tasks");
assert.match(view(), /Việc hôm nay/, "Tasks renders");
assert.doesNotMatch(view(), /class="filter-row"/, "No extra task filters");
click("data-route", "garden");
assert.equal((view().match(/class="plot(?: empty-plot)?"/g) ?? []).length, 5, "Five garden plots");
click("data-route", "shop");
assert.equal((view().match(/class="product"/g) ?? []).length, 11, "All eleven species");
click("data-shop-tab", "items");
assert.equal((view().match(/class="product"/g) ?? []).length, 2, "Care sample catalog");
click("data-route", "journal");
assert.match(view(), /Nhật ký/, "Journal renders");
click("data-route", "inventory");
assert.match(view(), /Túi đồ/, "Inventory renders");
click("data-route", "tree");
assert.match(view(), /Hành trình lớn lên/, "Tree care renders");
const pngs = [
  "simple_cherry.png", "simple_pine.png", "simple_mandarin.png",
  "simple_succulent.png", "simple_home.png", "simple_forest.png",
  "nursery_monstera.png", "nursery_ficus.png", "nursery_snake.png",
  "nursery_cactus.png", "nursery_aloe.png", "nursery_lavender.png",
  "nursery_pilea.png", "design_fertilizer.png", "design_water_can.png"
];
for (const png of pngs) {
  assert.ok(existsSync(new URL("../core-ui/src/main/res/drawable-nodpi/" + png, import.meta.url)), "Missing source asset: " + png);
}
console.log("HabiTree web smoke checks passed (7 views, 11 species, 5 plots, source assets).");
