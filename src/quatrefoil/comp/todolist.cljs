
(ns quatrefoil.comp.todolist
  (:require [quatrefoil.dsl.alias
             :refer
             [create-comp group box sphere point-light perspective-camera scene text]]))

(def comp-task
  (create-comp
   :task
   {}
   (fn [task idx]
     (fn [state mutate instant]
       (group
        {:params {:x 0, :y (* idx -4)}}
        (sphere
         {:params {:radius 2, :x -20},
          :material {:kind :mesh-lambert,
                     :opacity 0.3,
                     :color (if (:done? task) 0x905055 0x9050ff)},
          :event {:click (fn [event dispatch!] (dispatch! :toggle-task (:id task)))}})
        (box
         {:params {:width 32, :height 4, :depth 1, :opacity 0.5},
          :material {:kind :mesh-lambert, :color 0xcccccc},
          :event {:click (fn [event dispatch!]
                    (dispatch! :edit-task (js/prompt "New task:" (:text task))))}}
         (text
          {:params {:text (:text task), :size 3, :height 2},
           :material {:kind :mesh-lambert, :color 0xffcccc}}))
        (sphere
         {:params {:radius 2, :x 30},
          :material {:kind :mesh-lambert, :opacity 0.3, :color 0xff5050},
          :event {:click (fn [event dispatch!] (dispatch! :delete-task (:id task)))}}))))))

(def comp-todolist
  (create-comp
   :todolist
   {}
   (fn [tasks]
     (fn [state mutate! instant]
       (group
        {}
        (group
         {:params {:y 40, :x 0, :z 0}}
         (box
          {:params {:width 32, :height 4, :depth 1, :opacity 0.5},
           :material {:kind :mesh-lambert, :color 0xcccccc},
           :event {:click (fn [event dispatch!]
                     (dispatch! :add-task (js/prompt "Task content?")))}})
         (sphere
          {:params {:radius 4, :x 40},
           :material {:kind :mesh-lambert, :opacity 0.3, :color 0x9050ff},
           :event {}}))
        (group
         {:params {:y 30, :x 0, :z 0}}
         (->> (vals tasks)
              (map-indexed (fn [idx task] [(:id task) (comp-task task idx)]))
              (into {}))))))))
