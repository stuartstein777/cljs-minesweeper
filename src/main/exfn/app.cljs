(ns exfn.app
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [exfn.subscriptions]
            [exfn.events]
            [exfn.logic :as ms]))

(defn board []
  (let [board       @(rf/subscribe [:board])
        board-width (count board)
        side-length (/ 896 board-width)
        revealed    @(rf/subscribe [:revealed])
        flags       @(rf/subscribe [:flags])]
    [:div
     (for [x (range board-width)]
       [:div.row {:style {:width "100%"
                          :height side-length ;; take off 1 for border
                          :margin 0
                          :padding 0}}
        (for [y (range board-width)]
          (let [cell-contents (get-in board [x y])]
            [:div.board-cell {:style {:width  side-length
                                      :height side-length}
                              :class (cond
                                       (and (revealed [x y])
                                            (number? cell-contents))
                                       "revealed-cell"

                                       :else
                                       "unrevealed-cell")
                              :on-click #(rf/dispatch-sync [:cell-click [x y]])}
             (when (and (revealed [x y]) (> cell-contents 0))
               [:p.number {:style {:height side-length
                                   :width side-length}}
                (str cell-contents)])]))])]))

;; -- App -------------------------------------------------------------------------
(defn app []
  [:div.container
   [:h1 "Minesweeper"]
    [:div.row
     [:div.col
      [:div.board
       [board]]]
     [:div.col
      [:div.row
       [:div.col
        (let [game-over? @(rf/subscribe [:game-over?])]
          [:p.game-over 
           {:style {:display (if game-over? :inline :none)}}
           "Game over!"])]]]]])

;; -- After-Load --------------------------------------------------------------------
;; Do this after the page has loaded.
;; Initialize the initial db state.
(defn ^:dev/after-load start
  []
  (dom/render [app]
              (.getElementById js/document "app")))

(defn ^:export init []
  (start))

; dispatch the event which will create the initial state. 
(defonce initialize (rf/dispatch-sync [:initialize]))

(comment
  
  (rf/dispatch-sync [:set-game-over])

  (rf/dispatch-sync [:reset])
  
  [[:blank 1 :mine]
   [:blank 1 1]
   [:blank :blank :blank]]

  [[:blank  :blank  :blank :blank :blank]
   [:blank     1       1      1   :blank]
   [:blank     1    :mine     1   :blank]
   [:blank     1       1      1   :blank]
   [:blank  :blank  :blank :blank :blank]]
  
  )