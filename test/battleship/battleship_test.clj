(ns battleship.battleship-test
  (:require [clojure.test :refer :all]
            [battleship.battleship :refer :all]))

(def expected-board-output
  "12345678910\na:0000000000\nb:0000000000\nc:0000000000\nd:0000000000\ne:0000000000\nf:0000000000\ng:0000000000\nh:0000000000\ni:0000000000\nj:0000000001")

(def expected-board-output2
  "12345678910\na:1000000000\nb:0000000000\nc:0000000000\nd:0000000000\ne:0000000000\nf:0000000000\ng:0000000000\nh:0000000000\ni:0000000000\nj:0000000000")

(deftest formating-board-test
  (testing "that format board fn outputs correctly formatted str :j 10"
    (is (= expected-board-output
           (battleship.battleship/format-board
            (battleship.battleship/coord->target :j 10)))))
  (testing "that format board fn outputs correctly formatted str :a 1"
    (is (= expected-board-output2
           (battleship.battleship/format-board
            (battleship.battleship/coord->target :a 1))))))

(deftest won-test
  (testing "board2 landings matches board1 ships"
    (let [board1 {:board 2r1100000001 :landings 2r0000001001 :player
                  {:name "a" :type :human}}
          board2 {:board 2r0000000001 :landings 2r1100000001 :player
                  {:name "b" :type :human}}]
      (is (true?
           (battleship.battleship/won?
            (:board board1)
            (:landings board2))))))

  (testing "if board2 ships and board1 landings are empty is not a winner"
    (let [board1 {:board 2r1100000001 :landings 2r0000000000 :player
                  {:name "a" :type :human}}
          board2 {:board 2r0000000000 :landings 2r1000000000 :player
                  {:name "b" :type :human}}]
      (is (false?
           (battleship.battleship/won?
            (:board board2)
            (:landings board1)))))))
