(ns battleship.gen-test
  (:require [clojure.test :refer :all]
            [battleship.gen :refer :all]
            [battleship.gen :as gen]
            [battleship.innout :as inNout]))

(defn count-placed-indexes [field]
  (battleship.innout/base2str field))

(defn base2str [bi]
  (format "%0100d" (biginteger (.toString (.toBigInteger bi) 2))))

(deftest starter-field-gen-test
  (testing "That is returns a field"
    (let [f 0N]
      (is (not= f
             (starter-field-gen gen/ships f))))))

(deftest place-ship-test
  (testing "That is places a ship in the field"
    (let [f 2r00000000]
      (is (not (= f (place-ship f {:len 3 :n 1} :vertical)))))))

(deftest update-field-for-ship-test
  (testing "That is can place the ship verticaly from the starting index"
    (let [f 2r0
          e 2r10000000001000000000000000000000000000000000000000000000000000000000000000000000000000000]
      (is (= (base2str e) (base2str ((add-ship-to-field 10) f 2 11))))))
  (testing "That is can place the ship horizontally from the starting index"
    (let [f 2r0
          e 2r11100000000000000000000000000000000000000000000000000000000000000000000000000000000000000]
      (is (= (base2str e) (base2str ((add-ship-to-field 1) f 3 11)))))))

(deftest field-has-space-test
  (testing "When there is room, then is has enough space for a ship vertically"
    (let [s {:len 2 :n 1} i 0]
      (is (field-has-vert-space? 0N s i))))
  (testing "When there is room, then is has just enough space for a ship vertically"
    (let [s {:len 2 :n 1} i 79]
      (is (field-has-vert-space? 0N s i))))
  (testing "When there is no room, then is NOT enough space for a ship vertically"
    (let [s {:len 2 :n 1} i 80]
      (is (not (field-has-vert-space? 0N s i)))))
  (testing "When there is room, then is has enough space for a ship horizontally"
    (let [s {:len 3 :n 1} i 0]
      (is (field-has-horiz-space? 0N s i))))
  (testing "When there is room, then is has just enough space for a ship vertically"
    (let [s {:len 3 :n 1} i 6]
      (is (field-has-horiz-space? 0N s i))))
  (testing "When there is no room, then is NOT enough space for a ship vertically"
    (let [s {:len 3 :n 1} i 7]
      (is (not (field-has-horiz-space? 0N s i)))))
  (testing "When there is room, then is has just enough space for a ship vertically"
    (let [s {:len 3 :n 1} i 16]
      (is (field-has-horiz-space? 0N s i))))
  (testing "When there is no room, then is NOT enough space for a ship vertically"
    (let [s {:len 3 :n 1} i 17]
      (is (not (field-has-horiz-space? 0N s i))))))

