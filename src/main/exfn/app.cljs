(ns exfn.app
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [exfn.subscriptions]
            [exfn.events]
            [exfn.logic :as ms]
            [goog.string.format]))

(defn pad-zero [n]
  (if (< n 10)
    (str "0" n)
    (str n)))

(defn x->svg [x]
  (case x
    :mine [:svg {:xmlns   "http://www.w3.org/2000/svg"
                 :viewBox "0 0 570 540"
                 :width   "100%"
                 :fill    :brown}
           [:path {:d "M575.5 283.5c-13.13-39.11-39.5-71.98-74.13-92.35c-17.5-10.37-36.25-16.62-55.25-19.87c6-17.75 10-36.49 10-56.24c0-40.99-14.5-80.73-41-112.2c-2.5-3-6.625-3.623-10-1.75c-3.25 1.875-4.75 5.998-3.625 9.748c4.5 13.75 6.625 26.24 6.625 38.49c0 67.73-53.76 122.8-120 122.8s-120-55.11-120-122.8c0-12.12 2.25-24.74 6.625-38.49c1.125-3.75-.375-7.873-3.625-9.748c-3.375-1.873-7.502-1.25-10 1.75C134.7 34.3 120.1 74.04 120.1 115c0 19.75 3.875 38.49 10 56.24C111.2 174.5 92.32 180.8 74.82 191.1c-34.63 20.49-61.01 53.24-74.38 92.35c-1.25 3.75 .25 7.748 3.5 9.748c3.375 2 7.5 1.375 10-1.5c9.377-10.87 19-19.12 29.25-25.12c57.25-33.87 130.8-13.75 163.9 44.99c33.13 58.61 13.38 133.1-43.88 167.8c-10.25 6.123-22 10.37-35.88 13.37c-3.627 .875-6.377 4.25-6.377 8.123c.125 4 2.75 7.248 6.502 7.998c39.75 7.748 80.63 .7495 115.3-19.74c18-10.5 32.88-24.49 45.25-39.99c12.38 15.5 27.38 29.49 45.38 39.99c34.5 20.49 75.51 27.49 115.1 19.74c3.875-.75 6.375-3.998 6.5-7.998c0-3.873-2.625-7.248-6.375-8.123c-13.88-2.873-25.63-7.248-35.75-13.37c-57.38-33.87-77.01-109.2-44-167.8c33.13-58.73 106.6-78.85 164-44.99c10.12 6.123 19.75 14.25 29.13 25.12c2.5 2.875 6.752 3.5 10 1.5C575.4 291.2 576.9 287.2 575.5 283.5zM287.1 320.1c-26.5 0-48-21.49-48-47.99c0-26.49 21.5-47.99 48-47.99c26.5 0 48.01 21.49 48.01 47.99C335.1 298.6 314.5 320.1 287.1 320.1zM385 377.6c1.152 22.77 10.74 44.63 27.22 60.92c47.45-35.44 79.13-90.58 83.1-153.4c-22.58-6.173-45.69-2.743-65.57 8.76C424.7 326.9 408.5 355.1 385 377.6zM253.3 132.6c26.22-6.551 45.37-6.024 69.52 .0254c21.93-9.777 39.07-28.55 47.48-51.75C345 69.98 317.3 63.94 288.1 63.94c-29.18 0-56.96 5.986-82.16 16.84C214.3 103.1 231.4 122.8 253.3 132.6zM163.8 438.5c16.46-16.26 26.03-38.19 27.14-61.01c-23.49-21.59-39.59-50.67-44.71-83.6C126.9 282.7 103.8 278.8 80.67 285.1C84.64 347.9 116.3 403.1 163.8 438.5z"}]]

    :flag [:svg {:xmlns   "http://www.w3.org/2000/svg"
                 :viewBox "0 0 512 512"
                 :width   "75%"
                 :fill    :brown}
           [:path {:d "M64 496C64 504.8 56.75 512 48 512h-32C7.25 512 0 504.8 0 496V32c0-17.75 14.25-32 32-32s32 14.25 32 32V496zM476.3 0c-6.365 0-13.01 1.35-19.34 4.233c-45.69 20.86-79.56 27.94-107.8 27.94c-59.96 0-94.81-31.86-163.9-31.87C160.9 .3055 131.6 4.867 96 15.75v350.5c32-9.984 59.87-14.1 84.85-14.1c73.63 0 124.9 31.78 198.6 31.78c31.91 0 68.02-5.971 111.1-23.09C504.1 355.9 512 344.4 512 332.1V30.73C512 11.1 495.3 0 476.3 0z"}]]

    1    [:svg {:xmlns          "http://www.w3.org/2000/svg"
                :viewBox        "0 0 256 512"
                :width          "50%"
                :height         "50%"
                :fill           :blue
                :display        :inline-block
                :align-items    :center
                :padding 0
                :vertical-align :center
                :margin         "0 auto"}
          [:path {:d "M256 448c0 17.67-14.33 32-32 32H32c-17.67 0-32-14.33-32-32s14.33-32 32-32h64V123.8L49.75 154.6C35.02 164.5 15.19 160.4 5.375 145.8C-4.422 131.1-.4531 111.2 14.25 101.4l96-64c9.828-6.547 22.45-7.187 32.84-1.594C153.5 41.37 160 52.22 160 64.01v352h64C241.7 416 256 430.3 256 448z"}]]

    2     [:svg {:xmlns   "http://www.w3.org/2000/svg"
                 :viewBox "0 0 320 512"
                 :width   "50%"
                 :height  "50%"
                 :fill    :green}
           [:path {:d "M320 448c0 17.67-14.33 32-32 32H32c-13.08 0-24.83-7.953-29.7-20.09c-4.859-12.12-1.859-26 7.594-35.03l193.6-185.1c31.36-30.17 33.95-80 5.812-113.4c-14.91-17.69-35.86-28.12-58.97-29.38C127.4 95.83 105.3 103.9 88.53 119.9L53.52 151.7c-13.08 11.91-33.33 10.89-45.2-2.172C-3.563 136.5-2.594 116.2 10.48 104.3l34.45-31.3c28.67-27.34 68.39-42.11 108.9-39.88c40.33 2.188 78.39 21.16 104.4 52.03c49.8 59.05 45.2 147.3-10.45 200.8l-136 130H288C305.7 416 320 430.3 320 448z"}]]

    3     [:svg {:xmlns   "http://www.w3.org/2000/svg"
                 :viewBox "0 0 320 512"
                 :width   "50%"
                 :height  "50%"
                 :fill    :red}
           [:path {:d "M320 344c0 74.98-61.02 136-136 136H103.6c-46.34 0-87.31-29.53-101.1-73.48c-5.594-16.77 3.484-34.88 20.25-40.47c16.75-5.609 34.89 3.484 40.47 20.25c5.922 17.77 22.48 29.7 41.23 29.7H184c39.7 0 72-32.3 72-72s-32.3-72-72-72H80c-13.2 0-25.05-8.094-29.83-20.41C45.39 239.3 48.66 225.3 58.38 216.4l131.4-120.4H32c-17.67 0-32-14.33-32-32s14.33-32 32-32h240c13.2 0 25.05 8.094 29.83 20.41c4.781 12.3 1.516 26.27-8.203 35.19l-131.4 120.4H184C258.1 208 320 269 320 344z"}]]

    4     [:svg {:xmlns   "http://www.w3.org/2000/svg"
                 :viewBox "0 0 320 512"
                 :width   "50%"
                 :height  "50%"
                 :fill    "#050954"}
           [:path {:d "M384 334.2c0 17.67-14.33 32-32 32h-32v81.78c0 17.67-14.33 32-32 32s-32-14.33-32-32v-81.78H32c-10.97 0-21.17-5.625-27.05-14.89c-5.859-9.266-6.562-20.89-1.875-30.81l128-270.2C138.6 34.33 157.8 27.56 173.7 35.09c15.97 7.562 22.78 26.66 15.22 42.63L82.56 302.2H256V160c0-17.67 14.33-32 32-32s32 14.33 32 32v142.2h32C369.7 302.2 384 316.6 384 334.2z"}]]

    5     [:svg {:xmlns   "http://www.w3.org/2000/svg"
                 :viewBox "0 0 320 512"
                 :width   "50%"
                 :height  "50%"
                 :fill    :navy}
           [:path {:d "M320 344.6c0 74.66-60.73 135.4-135.4 135.4H104.7c-46.81 0-88.22-29.83-103-74.23c-5.594-16.77 3.469-34.89 20.23-40.48c16.83-5.625 34.91 3.469 40.48 20.23c6.078 18.23 23.08 30.48 42.3 30.48h79.95c39.36 0 71.39-32.03 71.39-71.39s-32.03-71.38-71.39-71.38H32c-9.484 0-18.47-4.203-24.56-11.48C1.359 254.5-1.172 244.9 .5156 235.6l32-177.2C35.27 43.09 48.52 32.01 64 32.01l192 .0049c17.67 0 32 14.33 32 32s-14.33 32-32 32H90.73L70.3 209.2h114.3C259.3 209.2 320 269.1 320 344.6z"}]]

    6     [:svg {:xmlns   "http://www.w3.org/2000/svg"
                 :viewBox "0 0 320 512"
                 :width   "50%"
                 :height  "50%"
                 :fill    "#10d8e3"}
           [:path {:d "M167.7 160.8l64.65-76.06c11.47-13.45 9.812-33.66-3.656-45.09C222.7 34.51 215.3 32.01 208 32.01c-9.062 0-18.06 3.833-24.38 11.29C38.07 214.5 0 245.5 0 320c0 88.22 71.78 160 160 160s160-71.78 160-160C320 234.4 252.3 164.9 167.7 160.8zM160 416c-52.94 0-96-43.06-96-96s43.06-95.1 96-95.1s96 43.06 96 95.1S212.9 416 160 416z"}]]

    7    [:svg {:xmlns   "http://www.w3.org/2000/svg"
                :viewBox "0 0 320 512"
                :width   "50%"
                :height  "50%"
                :fill    :black}
          [:path {:d "M315.6 80.14l-224 384c-5.953 10.19-16.66 15.88-27.67 15.88c-5.469 0-11.02-1.406-16.09-4.359c-15.27-8.906-20.42-28.5-11.52-43.77l195.9-335.9H32c-17.67 0-32-14.33-32-32s14.33-32 32-32h256c11.45 0 22.05 6.125 27.75 16.06S321.4 70.23 315.6 80.14z"}]]

    8    [:svg {:xmlns   "http://www.w3.org/2000/svg"
                :viewBox "0 0 320 512"
                :width   "50%"
                :height  "50%"
                :fill    "#4e4f4f"
                :display :block
                :margin  "0 auto"}
          [:path {:d "M267.5 249.2C290 226.1 304 194.7 304 160c0-70.58-57.42-128-128-128h-32c-70.58 0-128 57.42-128 128c0 34.7 13.99 66.12 36.48 89.19C20.83 272.5 0 309.8 0 352c0 70.58 57.42 128 128 128h64c70.58 0 128-57.42 128-128C320 309.8 299.2 272.5 267.5 249.2zM144 96.01h32c35.3 0 64 28.7 64 64s-28.7 64-64 64h-32c-35.3 0-64-28.7-64-64S108.7 96.01 144 96.01zM192 416H128c-35.3 0-64-28.7-64-64s28.7-64 64-64h64c35.3 0 64 28.7 64 64S227.3 416 192 416z"}]]))

(defn board []
  (let [board       @(rf/subscribe [:board])
        board-width (count board)
        side-length (/ 896 board-width)
        revealed    @(rf/subscribe [:revealed])
        flags       @(rf/subscribe [:flags])
        game-won?   @(rf/subscribe [:game-won?])
        game-over?  @(rf/subscribe [:game-over?])
        ticking?    @(rf/subscribe [:ticking?])
        started?    @(rf/subscribe [:started?])
        paused?    (and started? (not ticking?))]
    [:div
     (for [x (range board-width)]
       [:div.row {:style {:width "100%"
                          :height side-length
                          :margin 0
                          :padding 0}}
        (for [y (range board-width)]
          (let [cell-contents (get-in board [x y])]
            [:div.board-cell {:style {:width  side-length
                                      :height side-length}
                              :class (cond
                                       
                                       (and started? (not ticking?))
                                       "paused-cell"

                                       (and (revealed [x y])
                                            (number? cell-contents))
                                       "revealed-cell"

                                       :else
                                       "unrevealed-cell")
                              :on-click (fn [_]
                                          (when (not (or game-over? game-won? paused?))
                                            (rf/dispatch-sync [:start-timer])
                                            (rf/dispatch-sync [:cell-click [x y]])))
                              :on-context-menu (fn [e] 
                                                 (if (not (or game-over? game-won? paused?))
                                                   (do
                                                     (rf/dispatch [:start-timer])
                                                     (rf/dispatch-sync [:toggle-flag [x y]])
                                                     (.preventDefault e)
                                                     (.stopPropagation e))
                                                   (do
                                                     (.preventDefault e)
                                                     (.stopPropagation e))))}

             (cond

               (not ticking?)
               [:p]

               ;; The game is either over or won and the cell contains a mine.
               ;; then we want to show the mine.
               (and (or game-over? game-won?)
                    (= cell-contents :mine))
               [:p
                (x->svg :mine)]

               ;; the cell isn't revealed, but it has a flag on it, so show the flag.
               (and (not (revealed [x y])) (flags [x y]))
               [:p.flag {:style {:display :inline
                                 :text-align :center}}
                (x->svg :flag)]

               ;; the cell is revealed and its a mine number indicator
               ;; then show the number and colour it appropriately
               (and (revealed [x y]) (> cell-contents 0))
               [:p.number {:style {:height side-length
                                   :width side-length}}
                (x->svg cell-contents)])]))])]))

;; -- App -------------------------------------------------------------------------
(defn app []
  [:div.container
   [:div.row
    [:div.col.col-lg-8
     [:h1 "Minesweeper"]]
    [:div.col.col-lg-4 {:style {:text-align :right
                                :padding-right 50}}
     [:i.fab.fa-github]
     [:a {:href "https://github.com/stuartstein777/cljs-minesweeper"
          :style {:text-decoration :none}}
      " (repo) "]
     "|"
     [:a {:href "https://stuartstein777.github.io/"
          :style {:text-decoration :none}}
      " other projects"]]]
    [:div.row
     [:div.col
      [:div.board
       [board]]]
     [:div.col
      (let [game-over? @(rf/subscribe [:game-over?])
            mines      @(rf/subscribe [:mines])
            flags      @(rf/subscribe [:flags])
            game-won?  @(rf/subscribe [:game-won?])
            time       @(rf/subscribe [:time])
            paused?    @(rf/subscribe [:ticking?])
            started?   @(rf/subscribe [:started?])]
        [:div
         [:div.row
          [:i.fas.fa-flag.mines]]
         [:div.row
          [:p.mines (str (count flags) "/" (or mines 0))]]
         [:div.row
          [:i.fas.fa-clock.mines]]
         [:div.row
          [:p.mines (str (pad-zero (quot time 60)) ":" (pad-zero (rem time 60)))]
          (if paused?
            [:i.fas.fa-pause.mines {:style {:font-size "2.5em"
                                            :padding-bottom 20
                                            :cursor :pointer
                                            :display (if (and started? (not game-over?) (not game-won?))
                                                       :inline
                                                       :none)}
                                    :on-click (fn [_]
                                               (when (and (not game-over?) (not game-won?))
                                                (rf/dispatch [:pause])))}]
            
            [:i.fas.fa-play.mines {:style {:font-size      "2.5em"
                                           :padding-bottom 20
                                           :cursor         :pointer
                                           :display (if (and started? (not game-over?) (not game-won?))
                                                      :inline
                                                      :none)}
                                   :on-click (fn [_]
                                               (when (and (not game-over?) (not game-won?))
                                                 (rf/dispatch [:pause])))}])]
         [:div.row
          [:div {:style {:text-align :center}}
           [:button.btn-primary
            {:style {:width "150px"
                     :height "150px"
                     :text-align :center
                     :display :inline}
             :on-click (fn [_]
                         (rf/dispatch-sync [:stop-timer])
                         (rf/dispatch-sync [:initialize [16 40]]))}
            [:div "New Game"]
            [:div "(16x16 - 40 mines)"]]
           [:button.btn-primary
            {:style {:width "150px"
                     :height "150px"
                     :margin 10
                     :text-align :center
                     :display :inline}
             :on-click (fn [_]
                         (rf/dispatch-sync [:stop-timer])
                         (rf/dispatch-sync [:initialize [8 10]]))}
            [:div "New Game"]
            [:div "(8x8 - 10 mines)"]]]]
         [:div.row
          [:div {:style {:text-align :center}}
           [:button.btn-primary
            {:style {:width "150px"
                     :height "150px"
                     :text-align :center
                     :display :inline}
             :on-click (fn [_]
                         (rf/dispatch-sync [:stop-timer])
                         (rf/dispatch-sync [:initialize [24 50]]))}
            [:div "New Game"]
            [:div "(24x24 - 50 mines)"]]
           [:button.btn-primary
            {:style {:width "150px"
                     :height "150px"
                     :margin 10
                     :text-align :center
                     :display :inline}
             :on-click (fn [_]
                         (rf/dispatch-sync [:stop-timer])
                         (rf/dispatch-sync [:initialize [32 80]]))}
            [:div "New Game"]
            [:div "(32x32 - 80 mines)"]]]]
         [:div.row
          [:div {:style {:text-align :center}}
           [:p.game-over
            {:style {:display (if (or game-over? game-won?) :inline :none)
                     :color (if game-over? :red :yellow)}}
            (if game-over?
              "Game over!"
              [:div.winner
               [:div
                [:i.fas.fa-trophy]]
               "Winner!"])]]]])]]])

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

  "Some dev-test events for reseting board."
  (rf/dispatch-sync [:set-game-over])

  (do
    (rf/dispatch-sync [:stop-timer])
    (rf/dispatch-sync [:reset {:board      (ms/generate-full-board {:dimensions [8 8]
                                                                    :mines      10})
                               :mines      10
                               :revealed   #{}
                               :ticking?   false
                               :time       0
                               :ticker-handle nil
                               :game-over? false
                               :game-won?  false
                               :started?   false
                               :flags      #{}}]))
 

  )