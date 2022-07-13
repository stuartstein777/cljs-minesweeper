(ns exfn.events
  (:require [re-frame.core :as rf]
            [exfn.logic :as bf]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:board []}))

