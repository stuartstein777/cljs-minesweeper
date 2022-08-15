(ns exfn.logic
  (:require [clojure.set :as set]))

;; -----------------------------------------------------------------
;; Get the number of mines surrounding a given square [x y] on the
;; board.
;; 
;; Works by getting all the 8 surrounding neighbour-coords, then map
;; these over the board. Filter out the ones that are :mine and count
;; them
;;
(defn get-neighbour-mine-count [board [x y]]
  (let [neighbour-coords [[-1 1] [0 1] [1 1] [-1 0] [1 0] [-1 -1] [0 -1] [1 -1]]]
    (->> neighbour-coords
         (map (fn [[xd yd]] (get-in board [(+ x xd) (+ y yd)])))
         (filter (fn [b] (= b :mine)))
         (count))))

;; -----------------------------------------------------------------
;; Want a random board.
;; We know how many mines to make and the dimension [x y]
;; Create a list of n mines and then fill it with the required blank
;; squares and shuffle it.
;; Then partition by the width of the board to make a 2d grid
;;
(defn- generate-board [{:keys [dimensions mines]}]
  (let [[width height] dimensions]
    (->>
     (concat (repeat mines :mine)
             (repeat (- (* width height) mines) :blank))
     (shuffle)
     (partition width)
     (map vec)
     vec)))

;; -----------------------------------------------------------------
;; If its a mine, just return the current board state (acc)
;; Otherwise update the given square with the number of surrounding
;; mines
;;
(defn reducer [acc xy]
  (if (= (get-in acc xy) :mine)
    acc
    (assoc-in acc xy (get-neighbour-mine-count acc xy))))

;; -----------------------------------------------------------------
;; Generates x and y co-ords for every square on the board. e.g. a 3x3
;; board would generate
;;
;; ([0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 0] [2 1] [2 2])
;;
(defn get-all-cell-coords [board]
  (for [xs (range (count (first board)))
        ys (range (count board))]
    [xs ys]))

;; -----------------------------------------------------------------
;; Add the mine counts to the generated board.
;;
(defn add-mine-counts [board cell-coords]
  (reduce reducer board cell-coords))

;; -----------------------------------------------------------------
;; Generate a full game board. Mines and mine counts.
;;
(defn generate-full-board [board-options]
  (let [board (generate-board board-options)
        cell-coords (get-all-cell-coords board)]
    (add-mine-counts board cell-coords)))

;; -----------------------------------------------------------------
;; Get all the 8 neighbour co-ords of a given x and y, 
;; Filter them so none are outside the bounds of the board (max-x, max-y)
;;
(defn get-neighbours [max-x max-y [x y]]
  (set (remove (fn [[x y]] (or (neg? x) (neg? y) (>= x max-x) (>= y max-y)))
               [[x (dec y)] [(dec x) (dec y)] [(dec x) y]
                [(dec x) (inc y)] [x (inc y)] [(inc x) (inc y)]
                [(inc x) y] [(inc x) (dec y)]])))


(defn update-to-check [to-check neighbours revealed]
  (set/union to-check (set/difference neighbours revealed)))

;;-----------------------------------------------------------------
;; Reveal squares when the player clicks a square on the board.
;; Want to keep revealing squares until we hit a number.
;;
;; to-check :: a set of squares that need to be checked to see if they
;; are blank or a number.
;; revealed :: a set of squares that are revealed in the current game.
;; board    :: the current game board
;;
;; when a cell is revealed, get all the neighbour cells and add them to
;; the `to-check` collection (if they aren't already revealed) and recur.
;; We are done when the set of cells to check is empty.
(defn reveal [to-check revealed board]
  (let [get-neighbours (partial get-neighbours (count (first board)) (count board))]
    (if (empty? to-check)
      revealed
      (let [cur (first to-check)
            board-item (get-in board cur)]
        (cond (= 0 board-item)
              (let [neighbours (get-neighbours cur)
                    up-to-check (update-to-check to-check neighbours revealed)]
                (recur (set/difference up-to-check #{cur}) (conj revealed cur) board))

              :else
              (recur (set/difference to-check #{cur}) (conj revealed cur) board))))))

;;-----------------------------------------------------------------
;; The game is won when the count of revealed squares + count of mines
;; = the total number of squares on the board.
;;
(defn game-won? [revealed board mines]
  (let [total-cells (count board)]
    (= (+ (count revealed) mines)
       (* total-cells total-cells))))

(defn get-cell-if-mine [board cell]
  (if (= :mine (get-in board cell))
    cell
    nil))

;;-----------------------------------------------------------------
;; If the player lefts clicks a number, then we need to 
;; 1. Check if the number of flags in the neighbours is equal to the
;;    clicked number.
;; 2. If it is, then we need to check if all the non-flagged cells
;;    are mine free. 
;;
;; If both these are true, then we need to return the revealed cells.
;; 
;; Otherwise return :revealed-mine as an indicator its game over.
;;
(defn reveal-with-flags [flags board cell]
  (let [neighbours (get-neighbours (count board) (count board) cell)
        neighbours-flag-count (count (keep flags neighbours))
        cell-num (get-in board cell)]
    (if (= cell-num neighbours-flag-count)
      ;; get all the contents of revealed cells, if any contain a mine return 
      ;; :mine-revealed
      ;; else return the neighbour coords in a set.
      ;; dont include mines that are covered by flags
      (if (-> (keep (partial get-cell-if-mine board) neighbours)
              (set)
              (set/difference flags)
              empty?) ;; if set is empty, then no mines revealed
        (set/difference neighbours flags)
        :revealed-mine)
      #{})))
