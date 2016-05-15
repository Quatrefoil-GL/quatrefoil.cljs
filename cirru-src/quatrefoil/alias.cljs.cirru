
ns quatrefoil.alias

defn create-element (element-name props children)
  {} (:type :element)
    :attrs $ :attrs props
    :event $ :event props
    :matertial $ :material props
    :children $ ->> children
      map-indexed $ fn (index child)
        [] index child
      into $ sorted-map

defn create-comp (comp-name render-method)
  fn (& args)
    {} (:type :component)
      :name comp-name
      :args args
      :render $ apply render-method args

defn scene (props & children)
  create-element :scene props children

defn light (props & children)
  create-element :light props children

defn camera (props & children)
  create-element :camara props children

defn group (props & children)
  create-element :group props children

defn cube (props & children)
  create-element :cube props children
