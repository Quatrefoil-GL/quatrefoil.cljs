
ns quatrefoil.component.container $ :require
  [] quatrefoil.alias :refer $ [] scene light box group create-comp sphere

defn render (store)
  fn
    state mutate instant tick
    scene ({})
      light $ {}
        :args $ [] 0xaaaaff 1.6 200
        :attrs $ {}
          :position $ [] 10 10 40

      group ({})
        sphere $ {}
          :material $ {} (:kind :mesh-basic)
            :wireframe true
          :args $ [] 2 20 20
          :attrs $ {}
            :position $ [] 0 0 0

      group
        {} $ :attrs
          {} $ :rotation
            [] (* 0.1 js/Math.PI)
              , 0 0

        box $ {}
          :material $ {} (:kind :lambert)
          :args $ [] 4 2 2
          :attrs $ {}
            :position $ [] 8 0 0

        box $ {}
          :material $ {} (:kind :lambert)
          :args $ [] 2 4 2
          :attrs $ {}
            :position $ [] 0 6 0

        box $ {}
          :material $ {} (:kind :mesh-basic)
            :wireframe true
          :args $ [] 2 2 4
          :attrs $ {}
            :position $ [] 0 0 10

def comp-container $ create-comp :container render
