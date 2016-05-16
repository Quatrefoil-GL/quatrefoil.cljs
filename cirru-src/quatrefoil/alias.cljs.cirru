
ns quatrefoil.alias

defn create-element (element-name props children)
  {} (:type :element)
    :name element-name
    :args $ :args props
    :attrs $ :attrs props
    :event $ :event props
    :material $ :material props
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

defn group (props & children)
  create-element :group props children

defn box (props & children)
  create-element :box props children

defn sphere (props & children)
  create-element :sphere props children

defn line (props & children)
  create-element :line props children
