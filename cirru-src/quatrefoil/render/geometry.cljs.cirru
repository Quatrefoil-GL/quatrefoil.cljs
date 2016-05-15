
ns quatrefoil.render.geometry

defn render-geometry-dsl (kind args)
  {} $ :pre
    [] $ vector? args
  case kind
    :box $ let
      (([] x y z) args)

      println |xyz: x y z
      THREE.BoxGeometry. x y z

    throw $ str "|Geometry not found:" kind
