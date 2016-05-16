
ns quatrefoil.render.core $ :require
  [] quatrefoil.render.material :refer $ [] render-material-dsl
  [] quatrefoil.render.geometry :refer $ [] render-geometry-dsl

declare render-markup

defn render-element (markup)
  let
    (el $ case (:name markup) (:scene $ THREE.Scene.) (:light $ let ((([] color intensity distance) (:args markup)) (attrs $ :attrs markup) (light $ THREE.PointLight. color intensity distance)) (, light)) (:group $ THREE.Group.) (let ((geometry $ render-geometry-dsl (:name markup) (:args markup) (:attrs markup)) (material $ render-material-dsl (:material markup)) (mesh $ if (= :line $ :name markup) (THREE.Line. geometry material) (THREE.Mesh. geometry material))) (, mesh)))
      attrs $ :attrs markup

    doseq
      [] child-entry $ :children markup
      .add el $ render-markup (val child-entry)

    -- .log js/console "|result of el:" el
    if (contains? attrs :position)
      let
        (([] x y z) (:position attrs))

        .set el.position x y z

    if (contains? attrs :quaternion)
      let
        (([] x y z w) (:quaternion attrs))

        .set el.quaternion x y z w

    if (contains? attrs :rotation)
      let
        (([] x y z) (:rotation attrs))

        .set el.rotation x y z

    set! el.name $ pr-str (:coord markup)
    , el

defn render-component (markup)
  render-element $ :tree markup

defn render-markup (markup)
  if
    = :component $ :type markup
    render-component markup
    render-element markup
