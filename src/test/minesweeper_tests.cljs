(ns minesweeper-tests
  (:require [cljs.test :refer [deftest is testing run-tests]]
            [exfn.logic :as lc :refer [add-mine-counts get-all-cell-coords]]))

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
              [0  0  0 0 0]]))))
  )