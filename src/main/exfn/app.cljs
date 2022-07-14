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
                              :on-click #(rf/dispatch-sync [:cell-click [x y]])
                              :on-context-menu (fn [e] 
                                                 (rf/dispatch-sync [:toggle-flag [x y]])
                                                 (.preventDefault e)
                                                 (.stopPropagation e))}

             (cond
               (and (revealed [x y]) (> cell-contents 0))
               [:p.number {:style {:height side-length
                                   :width side-length
                                   :color (ms/get-num-colour cell-contents)}}
                (str cell-contents)]

               (and (not (revealed [x y])) (flags [x y]))
               [:p.flag {:style {:display :inline
                                 :text-align :center}}
                [:i.fas.fa-flag
                 {:style {:display :inline
                          :text-align :center}}]]
               
               :else
               [:p.debug cell-contents])]))])]))

;; -- App -------------------------------------------------------------------------
(defn app []
  [:div.container
   [:h1 "Minesweeper"]
    [:div.row
     [:div.col
      [:div.board
       [board]]]
     [:div.col
      (let [game-over? @(rf/subscribe [:game-over?])
            mines @(rf/subscribe [:mines])
            flags @(rf/subscribe [:flags])]
        [:div
         [:div.row
          [:i.fas.fa-flag.mines]]
         [:div.row
          [:p.mines (str (count flags) "/" mines)]]
         [:div.row
          [:i.fas.fa-clock.mines]]
         [:div.row
          [:p.mines "00:00"]]
         [:div.row
          [:button.btn-primary
           {:style {:width "200px"
                    :text-align :center
                    :display :inline}
            :on-click #(rf/dispatch-sync [:initialize])}
           "New Game"]]
         [:div.row
          [:p.game-over
           {:style {:display (if game-over? :inline :none)}}
           "Game over!"]]])]]])

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

  (rf/dispatch-sync [:reset {:board (ms/generate-full-board {:dimensions [16 16] :mines 40})
                             :mines 40
                             :revealed #{}
                             :game-over? false
                             :flags #{}}])

  (rf/dispatch-sync [:reset {:board [[0 1 :mine]
                                     [0 1 1]
                                     [0 0 0]]
                             :mines 1
                             :flags #{}
                             :revealed #{}
                             :game-over? false}])
  
  (count [[0 1]
          [0 10]
          [0 8]
          [5 3]
          [0 7]
          [0 9]
          [8 11]
          [12 10]
          [2 6]
          [6 11]
          [3 12]
          [1 9]
          [10 4]
          [5 8]
          [9 8]
          [13 7]
          [7 6]])
  )