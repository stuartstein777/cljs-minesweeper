(ns exfn.subscriptions
  (:require [re-frame.core :as rf]))

(rf/reg-sub 
 :revealed
 (fn [db _] (db :revealed)))

(rf/reg-sub
 :flags
 (fn [db _] (db :flags)))

(rf/reg-sub
 :running?
 (fn [db _] (db :running?)))

(rf/reg-sub
 :board
 (fn [db _] (db :board)))

(rf/reg-sub
 :game-over?
 (fn [db _] (db :game-over?)))

(rf/reg-sub
 :mines
 (fn [db _] (db :mines)))

(rf/reg-sub
 :game-won?
 (fn [db _] (db :game-won?)))

(rf/reg-sub
 :time
 (fn [db _] (db :time)))

(rf/reg-sub
 :ticking?
 (fn [db _] (db :ticking?)))

(rf/reg-sub
 :started?
 (fn [db _] (db :started?)))