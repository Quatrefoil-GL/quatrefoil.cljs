
Quatrefoil
----

> Render Three.js with Respo style code.

### Patching operations

* [ ] `add-params`
* [ ] `remove-params`
* [ ] `update-params`
* [ ] `add-events`
* [ ] `remove-events`
* [ ] `replace-material`
* [ ] `add-material`
* [ ] `remove-material`
* [x] `update-material`
* [ ] `add-element`
* [ ] `remove-element`
* [ ] `replace-element`
* [x] `add-children`
* [x] `remove-children`

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
