(ns battleship.innout-test
  (:require [clojure.test :refer [deftest testing is]]
            [battleship.innout :as inNout]
            [battleship.convert :as convert]))

(def expected-board-output
  "12345678910\na:0000000000\nb:0000000000\nc:0000000000\nd:0000000000\ne:0000000000\nf:0000000000\ng:0000000000\nh:0000000000\ni:0000000000\nj:0000000001")

(def expected-board-output2
  "12345678910\na:1000000000\nb:0000000000\nc:0000000000\nd:0000000000\ne:0000000000\nf:0000000000\ng:0000000000\nh:0000000000\ni:0000000000\nj:0000000000")

(deftest formating-board-test
  (testing "that format board fn outputs correctly formatted str :j 10"
    (is (= expected-board-output
           (inNout/format-board
            (convert/coord->target :j 10)))))
  (testing "that format board fn outputs correctly formatted str :a 1"
    (is (= expected-board-output2
           (inNout/format-board
            (convert/coord->target :a 1))))))

(deftest base2str-test
  (testing "base2str returns a properly padded version of the BigInt passed in"
    (let [expected (apply str (take 100 (repeat "0")))]
      (is (= expected (inNout/base2str 0N))))))
