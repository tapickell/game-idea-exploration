(ns battleship.battleship-test
  (:require [clojure.test :refer [deftest testing is]]
            [battleship.battleship :as bs]))

(deftest won-test
  (testing "board2 landings matches board1 ships"
    (let [board1 {:board 2r1100000001 :landings 2r0000001001 :player
                  {:name "a" :type :human}}
          board2 {:board 2r0000000001 :landings 2r1100000001 :player
                  {:name "b" :type :human}}]
      (is (true?
           (bs/won?
            (:board board1)
            (:landings board2))))))

  (testing "if board2 ships and board1 landings are empty is not a winner"
    (let [board1 {:board 2r1100000001 :landings 2r0000000000 :player
                  {:name "a" :type :human}}
          board2 {:board 2r0000000000 :landings 2r1000000000 :player
                  {:name "b" :type :human}}]
      (is (false?
           (bs/won?
            (:board board2)
            (:landings board1)))))))
