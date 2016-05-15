
ns quatrefoil.component.container $ :require
  [] quatrefoil.alias :refer $ [] scene light camera cube group create-comp

defn render (store)
  fn (state mutate)
    fn (instant tick)
      scene ({})
        camera $ {}
        group ({})
          light $ {}
        group ({})
          cube $ {}

def comp-container $ create-comp :container render
