(ns exfn.events
  (:require [re-frame.core :as rf]
            [exfn.logic :as bf]
            [clojure.set :as set]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:board (bf/generate-full-board {:dimensions [16 16] :mines 40})
    :revealed #{}
    :flags #{}
    :mines 40
    :running? false
    :game-over? false}))

(rf/reg-event-db
 :cell-click
 (fn [{:keys [board revealed] :as db} [_ [x y]]]
   (prn x y)
   (let [contents (get-in board [x y])]
     (cond

       (= contents :mine)
       (-> db (assoc :game-over? true))

       (= contents 0)
       (let [revealed (bf/reveal #{[x y]} revealed board)]
         (update db :revealed set/union revealed))

       :else
       (update db :revealed set/union #{[x y]})))))

;; dev test events. To remove.

(rf/reg-event-db
 :set-game-over
 (fn [db _]
   (assoc db :game-over? (not (db :game-over?)))
   ))

(rf/reg-event-db
 :reset
 (fn [db [_ game]]
   game))

(comment
  (let [db {:revealed #{[1 1] [2 2] [3 3]}}
        revealed #{[2 2] [4 3] [1 0] [1 1] [3 3]}]
    
    (-> db
        (update :revealed set/union revealed))
    )
  
  )