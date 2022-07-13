(ns exfn.logic
  (:require [clojure.set :as set]))

(defn get-neighbour-mine-count [board [x y]]
  (let [neighbour-coords [[-1 1] [0 1] [1 1] [-1 0] [1 0] [-1 -1] [0 -1] [1 -1]]]
    (->> neighbour-coords
         (map (fn [[xd yd]] (get-in board [(+ x xd) (+ y yd)])))
         (filter (fn [b] (= b :mine)))
         (count))))

(defn- generate-board [{:keys [dimensions mines]}]
  (let [[width height] dimensions]
    (->>
     (concat (repeat mines :mine)
             (repeat (- (* width height) mines) :blank))
     (shuffle)
     (partition width)
     (map vec)
     vec)))

(defn reducer [acc xy]
  (if (= (get-in acc xy) :mine)
    acc
    (assoc-in acc xy (get-neighbour-mine-count acc xy))))

(defn get-all-cell-coords [board]
  (for [xs (range (count (first board)))
        ys (range (count board))]
    [xs ys]))

(defn add-mine-counts [board cell-coords]
  (reduce reducer board cell-coords))

(defn generate-full-board [board-options]
  (let [board (generate-board board-options)
        cell-coords (get-all-cell-coords board)]
    (add-mine-counts board cell-coords)))

(defn get-neighbours [max-x max-y [x y]]
  (set (remove (fn [[x y]] (or (neg? x) (neg? y) (>= x max-x) (>= y max-y)))
               [[x (dec y)] [(dec x) (dec y)] [(dec x) y]
                [(dec x) (inc y)] [x (inc y)] [(inc x) (inc y)]
                [(inc x) y] [(inc x) (dec y)]])))

(defn update-to-check [to-check neighbours revealed]
  (set/union to-check (set/difference neighbours revealed)))

(defn reveal [to-check revealed board]
  (let [get-neighbours (partial get-neighbours (count (first board)) (count board))]
    (if (empty? to-check)
      revealed
      (let [cur (first to-check)
            board-item (get-in board cur)]
        (cond (= :blank board-item)
              (let [neighbours (get-neighbours cur)
                    up-to-check (update-to-check to-check neighbours revealed)]
                (recur (set/difference up-to-check #{cur}) (conj revealed cur) board))

              (= :mine board-item)
              (recur (set/difference to-check #{cur}) revealed board)

              :else
              (recur (set/difference to-check #{cur}) (conj revealed cur) board))))))
