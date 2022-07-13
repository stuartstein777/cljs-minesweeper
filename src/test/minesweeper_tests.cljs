(ns minesweeper-tests
  (:require [cljs.test :refer [deftest is testing run-tests]]
            [exfn.logic :as lc :refer [add-mine-counts get-all-cell-coords reveal]]))

(deftest test-generating-mine-counts
  (testing "surrounded"
    (prn "testing surrounded")
    (let [board [[:mine :mine  :mine]
                 [:mine :blank :mine]
                 [:mine :mine  :mine]]
          cells (get-all-cell-coords board)]
      (is (= (add-mine-counts board cells)
             [[:mine :mine  :mine]
              [:mine 8 :mine]
              [:mine :mine  :mine]]))
      ))
  (testing "checkerboard"
    (prn "testing checkerboard")
    (let [board [[:mine :blank  :mine :blank :mine]
                 [:blank  :mine :blank :mine :blank]
                 [:mine :blank  :mine :blank :mine]
                 [:blank  :mine :blank :mine :blank]
                 [:mine :blank  :mine :blank :mine]]
          cells (get-all-cell-coords board)]
      (is (= (add-mine-counts board cells)
             [[:mine 3  :mine 3 :mine]
              [3  :mine 4 :mine 3]
              [:mine 4  :mine 4 :mine]
              [3 :mine 4 :mine 3]
              [:mine 3  :mine 3 :mine]]))))
  (testing "sparse"
    (prn "testing sparse")
    (let [board [[:blank  :blank  :blank :blank :blank]
                 [:blank  :blank  :blank :mine  :blank]
                 [:blank  :blank  :mine  :blank :blank]
                 [:blank  :blank  :blank :mine  :blank]
                 [:mine   :blank  :blank :blank :blank]]
          cells (get-all-cell-coords board)]
      (is (= (add-mine-counts board cells)
             [[0  0  1 1 1]
              [0  1  2 :mine  1]
              [0  1  :mine  3 2]
              [1  2  2 :mine  1]
              [:mine  1  1 1 1]]))))
  
  (testing "very sparse"
    (prn "very sparse")
    (let [board [[:blank  :blank  :blank :blank :blank]
                 [:blank  :blank  :blank :blank :blank]
                 [:blank  :blank  :mine  :blank :blank]
                 [:blank  :blank  :blank :blank :blank]
                 [:blank  :blank  :blank :blank :blank]]
          cells (get-all-cell-coords board)]
      (is (= (add-mine-counts board cells)
             [[0  0  0 0 0]
              [0  1  1 1 0]
              [0  1  :mine  1 0]
              [0  1  1 1  0]
              [0  0  0 0 0]]))))) 
              
(deftest testing-reveal
  (testing "revealing board"
    (prn "revealing simple board")
    (let [board    [[:blank  :blank  :blank :blank :blank]
                    [:blank     1       1      1   :blank]
                    [:blank     1    :mine     1   :blank]
                    [:blank     1       1      1   :blank]
                    [:blank  :blank  :blank :blank :blank]]
          revealed (reveal #{[0 0]} #{} board)
          expected #{[0 0] [0 1] [0 2] [0 3] [0 4] [1 0] [1 1] [1 2] [1 3] [1 4] [2 0] [2 1] [2 3] [2 4] [3 0] [3 1] [3 2] [3 3] [3 4] [4 0] [4 1] [4 2] [4 3] [4 4]}]
      (is (= revealed expected))))
  
  (testing "revealing number"
    (prn "revealing number")
    (let [board    [[:blank  :blank  :blank :blank :blank]
                    [:blank     1       1      1   :blank]
                    [:blank     1    :mine     1   :blank]
                    [:blank     1       1      1   :blank]
                    [:blank  :blank  :blank :blank :blank]]
          revealed (reveal #{[1 1]} #{} board)
          expected #{[1 1]}]
      (is (= revealed expected)))))
