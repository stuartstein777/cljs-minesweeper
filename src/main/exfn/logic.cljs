(ns exfn.logic)

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