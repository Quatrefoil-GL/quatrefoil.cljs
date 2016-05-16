
ns quatrefoil.render.geometry

defn render-geometry-dsl (kind args attrs)
  {} $ :pre
    [] $ vector? args
  case kind
    :box $ let
      (([] x y z) args)

      THREE.BoxGeometry. x y z

    :sphere $ let
      (([] radius w-segments h-segments) args)

      THREE.SphereGeometry. radius w-segments h-segments

    :line $ let
      (vertices $ :vertices attrs) (geometry $ THREE.Geometry.)
        vertices $ ->> (:vertices attrs)
          map $ fn (point-vector)
            let
              (([] x y z) point-vector)

              THREE.Vector3. x y z

          apply js/Array.

      set! geometry.vertices vertices
      println vertices
      , geometry

    throw $ str "|Geometry not found:" kind
