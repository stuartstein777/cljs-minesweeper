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
    :game-won? false
    :game-over? false}))

(rf/reg-event-db
 :cell-click
 (fn [{:keys [board revealed mines] :as db} [_ [x y]]]
   (prn x y)
   (let [contents (get-in board [x y])]
     (cond

       (= contents :mine)
       (-> db (assoc :game-over? true))

       (= contents 0)
       (let [new-revealed (bf/reveal #{[x y]} revealed board)
             up-revealed (set/union new-revealed revealed)]
         (-> db
             (assoc :revealed up-revealed)
             (assoc :flags (set/difference (db :flags) up-revealed))
             (assoc :game-won? (bf/game-won? up-revealed board mines))))

       :else
       (let [up-revealed (set/union revealed #{[x y]})]
         (-> db
             (assoc :revealed up-revealed)
             (assoc :flags (set/difference (db :flags) #{[x y]}))
             (assoc :game-won? (bf/game-won? up-revealed board mines))))))))

(rf/reg-event-db
 :toggle-flag
 (fn [{:keys [flags] :as db} [_ cell]]
   (let [up-flags (if (flags cell)
                    (set/difference flags #{cell})
                    (conj flags cell))]
     (assoc db :flags up-flags))))

;; dev test events. To remove.

(rf/reg-event-db
 :set-game-over
 (fn [db _]
   (assoc db :game-over? (not (db :game-over?)))
   ))

(rf/reg-event-db
 :reset
 (fn [_ [_ game]]
   game))
