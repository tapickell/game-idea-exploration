(ns battleship.convert-test
  (:require [clojure.test :refer [deftest testing is]]
            [battleship.convert :refer [index->coord input->coord coord->target]]))

(deftest ^:conversion index->coord-test
  (testing ":a 1 -given a valid index it returns a valid coordinate vector"
    (let [expected [:a 1]]
      (is (= expected (index->coord 0)))))
  (testing ":j 10 -given a valid index it returns a valid coordinat vector"
    (let [expected [:j 10]]
      (is (= expected (index->coord 99)))))
  (testing "-1 -given an out of range index it throws AssertionError"
      (is (thrown? AssertionError (index->coord -1))))
  (testing "100 -given an out of range index it throws AssertionError"
      (is (thrown? AssertionError (index->coord 100)))))

(deftest ^:conversion input->coord-test
  (testing "valid input is converted to a valid coordinate"
    (let [expected [:a 1]]
          (is (= expected (input->coord "a 1")))))
  (testing "11 - invalid input is throws an AssertionError"
          (is (thrown? AssertionError (input->coord "a 11"))))
  (testing "0 - invalid input is throws an AssertionError"
          (is (thrown? AssertionError (input->coord "a 0"))))
  (testing "k - invalid input is throws an AssertionError"
          (is (thrown? AssertionError (input->coord "k 1")))))

(deftest coord->target-test
  (testing "valid coordinate is converted to a BigInt"
    (let [expected 0N]
      (is (= expected (coord->target :j 10))))))
