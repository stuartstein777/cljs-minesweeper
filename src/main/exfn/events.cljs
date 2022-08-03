(ns exfn.events
  (:require [re-frame.core :as rf]
            [exfn.logic :as bf]
            [clojure.set :as set]))

(defn dispatch-timer-event []
  (rf/dispatch [:tick]))

(rf/reg-event-db
 :initialize
 (fn [_ [_ [size mines]]]
   {:board (bf/generate-full-board {:dimensions [size size] :mines mines})
    :revealed #{}
    :flags #{}
    :mines mines
    :ticking? false
    :ticker-handle nil
    :started false
    :time 0
    :game-won? false
    :game-over? false}))

(rf/reg-event-db
 :set-handle
 (fn [db [_ handle]]
   (assoc db :ticker-handle handle)))

;; on tick, update the db time (in seconds) if we are currently ticking (not paused).
(rf/reg-event-db
 :tick
 (fn [{:keys [ticking?] :as db} _]
   (if ticking?
     (update db :time inc)
     db)))

(rf/reg-fx
 :start-ticker-incrementer
 (fn [[handle]]
   (when (nil? handle)
     (rf/dispatch [:set-handle (js/setInterval dispatch-timer-event 1000)]))))

(rf/reg-fx
 :stop-ticker
 (fn [[handle]]
   (when (not (nil? handle))
     (js/clearInterval handle))))

(rf/reg-event-fx
 :start-timer
 (fn [{:keys [db]} _]
   {:db (assoc db :ticking? true)    
    :start-ticker-incrementer [(db :ticker-handle)]}))

(rf/reg-event-fx
 :stop-timer
 (fn [{:keys [db]} _]
   (let [handle (db :ticker-handle)]
     {:db (assoc db :ticker-handle nil)
      :stop-ticker [handle]})))

(rf/reg-event-db
 :pause
 (fn [db _]
   (update db :ticking? not)))

(rf/reg-fx
 :stop-if-game-finished
 (fn [[finished? handle]]
   (when finished?
     (prn "finished. So stop handle")
     (js/clearInterval handle))))

(defn reveal-numbers-with-flags
  [{:keys [board mines] :as db} [x y]]
  (let [revealed (bf/reveal-with-flags (db :flags) (db :board) [x y])]
    (if (= :revealed-mine revealed)
      (assoc db :game-over? true)
      (let [up-revealed   (set/union revealed (db :revealed))
            also-revealed (bf/reveal revealed (db :revealed) board)
            fin-revealed  (set/union up-revealed also-revealed)
            game-won?     (bf/game-won? fin-revealed board mines)
            updated-db    (-> db
                              (assoc :revealed fin-revealed)
                              (update :flags set/difference fin-revealed)
                              (assoc :game-won? game-won?))]
        updated-db))))

(defn reveal-blank
  [{:keys [board revealed mines] :as db} [x y]]
  (let [new-revealed (bf/reveal #{[x y]} revealed board)
        up-revealed (set/union new-revealed revealed)]
    (-> db
        (assoc :revealed up-revealed)
        (assoc :flags (set/difference (db :flags) up-revealed))
        (assoc :game-won? (bf/game-won? up-revealed board mines)))))

(defn reveal-numbers
  [{:keys [revealed board mines] :as db} [x y]]
  (let [up-revealed (set/union revealed #{[x y]})
        game-won? (bf/game-won? up-revealed board mines)]
    (-> db
        (assoc :revealed up-revealed)
        (assoc :flags (set/difference (db :flags) #{[x y]}))
        (assoc :game-won? game-won?))))

(rf/reg-event-fx
 :cell-click
 (fn [{:keys [db]} [_ [x y]]]
   (let [contents (get-in (db :board) [x y])
         up-db    (cond

                  ;; clicked a mine, game over :(
                    (= contents :mine)
                    (assoc db :game-over? true)

                  ;; clicked a blank, reveal all cells until numbers.
                    (= contents 0)
                    (reveal-blank db [x y])

                  ;; clicked a number, that isn't a blank cell. Reveal with flags
                  ;; the cell also has to be revealed
                    (and (number? contents) ((db :revealed) [x y]))
                    (reveal-numbers-with-flags db [x y])

                  ;; clicked an unrevealed number, so reveal it.
                    :else
                    (reveal-numbers db [x y]))]
     {:db                    (assoc up-db :started? true)
      :stop-if-game-finished [(or (up-db :game-over?) (up-db :game-won?)) (up-db :ticker-handle)]})))

(rf/reg-event-db
 :toggle-flag
 (fn [{:keys [flags] :as db} [_ cell]]
   (let [up-flags (if (flags cell)
                    (set/difference flags #{cell})
                    (conj flags cell))]
     (-> db
         (assoc :flags up-flags)
         (assoc :started? true)))))

;; dev test events. To remove.

(rf/reg-event-db
 :set-game-over
 (fn [db _]
   (assoc db :game-over? (not (db :game-over?)))))

(rf/reg-event-db
 :reset
 (fn [_ [_ game]]
   game))
