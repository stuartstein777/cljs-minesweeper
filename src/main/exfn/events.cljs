(ns exfn.events
  (:require [re-frame.core :as rf]
            [exfn.logic :as bf]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:board (bf/generate-full-board {:dimensions [16 16] :mines 40})
    :revealed #{}
    :flags #{}
    :running? false}))
