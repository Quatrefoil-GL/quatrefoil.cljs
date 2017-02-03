
Quatrefoil
----

> Render Three.js with Respo style code.

### Patching operations

* [x] `add-element`
* [x] `remove-element`
* [x] `replace-element`
* [x] `add-children`
* [x] `remove-children`
* [x] `add-material`
* [x] `update-material`
* [ ] `replace-material`
* [ ] `remove-material`
* [x] `add-params`(partial)
* [x] `update-params`
* [ ] `remove-params`
* [ ] `add-events`
* [ ] `remove-events`

### Develop

```bash
boot build-advanced
webpack
export boot_deps=`boot show -c`
lumo -Kc $boot_deps:src/ -i render.cljs
```

Workflow https://github.com/mvc-works/stack-workflow

### License

MIT
