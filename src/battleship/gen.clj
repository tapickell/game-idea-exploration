(ns battleship.gen
  (:require [battleship.convert :as cvt]
            [battleship.ai :as ai]
            [battleship.innout :as inNout]))

(defn user-type [in]
  (if (re-matches #"[yY]" in) :human :pc))

(def empty-field
  "This is an 8 bit zero to start from."
  2r00000000)

;; Random ship placement
;; There are 5 ships to be placed: 1 5len, 1 4len, 2 3len, 1 2len
;; [{:len 5} {:len 4} {:len 3} {:len 3} {:len 2}]
;; The possible combinations of placing these ships are 1_925_751_392 -> 1.9 billion+
;; https://wiki.haskell.org/Battleship_game_combinatorics
;; Implement a way to turn a binary representation of a board into a mapping of coords and intersection value
;; 2r0000111100001111 -> {[:a 1] 0, [:a 2] 0, [:a 3] 0, [:a 4] 0, [:a 5] 1, [:a 6] 1,....}
;; (update m [:a 2] 1)
;; {:a {1 0, 2 0, 3 0..} :b {1 0, 2 0,..}} (update-in m [:a 2] 1)
;; pick a random ship length from avail ships
;; pick a random direction from hd of ship (horiz | vert)
;; constrain to subset of coords that are ship length away from an edge in that direction
;; (if vert and hd is north then can only place length distance from south wall)
;;  coords increase on the row index :a,:b,:c
;; (if horiz and hd is west then can only place length distance from east wall)
;;  coords increase on the column index 1,2,3
(def ships [{:len 5 :n 1} {:len 4 :n 1} {:len 3 :n 1} {:len 3 :n 2} {:len 2 :n 1}])

(defn field-has-space? [idx-inc]
  (fn [field ship index]
    (let [length (:len ship)
          next (+ index idx-inc 1)
          indexes (vec (range next (+ next (* idx-inc (- length 1)))))]
      (every? (fn [i] (= \0 (get (inNout/base2str field) i))) indexes))))

((field-has-space? 10) 0N {:len 4} 59)
((field-has-space? 10) 0N {:len 4} 60)

(defn field-has-vert-space? [field ship index]
  ((field-has-space? 10) field ship index))

(field-has-vert-space? 0N {:len 4} 59)
(field-has-vert-space? 0N {:len 4} 60)

(defn field-has-horiz-space? [field ship index]
  (let [ones (+ 1 (mod index 10))
        length (:len ship)]
    (and
     (<= (+ length ones) 10)
     ((field-has-space? 1) field ship index))))

(field-has-horiz-space? 0N {:len 4} 15)
(field-has-horiz-space? 0N {:len 4} 16)

(defn add-ship-to-field [idx-inc]
  (fn [field length index]
    (if (<= length 0)
      field
      (let [[r c] (cvt/index->coord index)
            target (cvt/coord->target r c)]
        (recur (+ field target) (- length 1) (+ index idx-inc))))))

(inNout/base2str ((add-ship-to-field 1) 0 5 0))
;; => "1111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
(inNout/base2str ((add-ship-to-field 10) 0 5 24))
;; => "0000000000000000000000001000000000100000000010000000001000000000100000000000000000000000000000000000"

;; DONE - need to prevent overlapping on the rest of the ship placed after hd
;; Can to check those tail indexes as well and reject the head-index if it will overlap
;; Could be done in the has-space fn since those are specific to the direction of the ship
(defn rand-place-ship [field ship has-space? add-ship]
  (loop [head-index (ai/find-empty-index (inNout/base2str field) (fn [x] x))
         tried []]
    (if (has-space? field ship head-index)
      (add-ship field (:len ship) head-index)
      (recur (ai/find-empty-index (inNout/base2str field) (fn [x] x)) (conj tried head-index)))))

(defn place-ship [field ship direction]
  (case direction
    :vertical (rand-place-ship field ship field-has-vert-space? (add-ship-to-field 10))
    :horizontal (rand-place-ship field ship field-has-horiz-space? (add-ship-to-field 1))))

(inNout/base2str (place-ship 0N {:len 3} :vertical))
;; => "0000000000000000000000100000000010000000001000000000000000000000000000000000000000000000000000000000"
(inNout/base2str (place-ship 0N {:len 3} :horizontal))
;; => "0000111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"

(defn get-from [coll idx]
  (let [element (get coll idx)
        updated-coll (vec (remove #{element} coll))]
    [element updated-coll]))

(defn starter-field-gen [available-ships field]
    (if (empty? available-ships)
      field
      (let [ridx (rand-int (count available-ships))
            [ship remaining-ships] (get-from available-ships ridx)
            direction (get [:horizontal :vertical] (rand-int 2))
            updated-field (place-ship field ship direction)]
        (recur remaining-ships updated-field))))

(starter-field-gen ships 0N)
;; => 1248463134672511748071676182528N

(defn place-ships
  "TODO - Randomly place ships on field, could use pre-generated fields"
  [field]
  field)

(defn create-player [name human?]
  {:name name :type (user-type human?)})

(defn create-board
  "Create a board for the player passed in.
   This will have ships randomly placed without collision."
  [player]
  {:board (place-ships empty-field) :landings empty-field :player player})

(into (sorted-map)
      (zipmap
       (for [a [:a :b :c :d :e :f :g :h :i :j] n (range 1 11)]
         [a n])
       (repeat 0)))
;; => {[:a 1] 0, [:a 2] 0, [:a 3] 0, [:a 4] 0, [:a 5] 0, [:a 6] 0, [:a 7] 0, [:a 8] 0, [:a 9] 0, [:a 10] 0, [:b 1] 0, [:b 2] 0, [:b 3] 0, [:b 4] 0, [:b 5] 0, [:b 6] 0, [:b 7] 0, [:b 8] 0, [:b 9] 0, [:b 10] 0, [:c 1] 0, [:c 2] 0, [:c 3] 0, [:c 4] 0, [:c 5] 0, [:c 6] 0, [:c 7] 0, [:c 8] 0, [:c 9] 0, [:c 10] 0, [:d 1] 0, [:d 2] 0, [:d 3] 0, [:d 4] 0, [:d 5] 0, [:d 6] 0, [:d 7] 0, [:d 8] 0, [:d 9] 0, [:d 10] 0, [:e 1] 0, [:e 2] 0, [:e 3] 0, [:e 4] 0, [:e 5] 0, [:e 6] 0, [:e 7] 0, [:e 8] 0, [:e 9] 0, [:e 10] 0, [:f 1] 0, [:f 2] 0, [:f 3] 0, [:f 4] 0, [:f 5] 0, [:f 6] 0, [:f 7] 0, [:f 8] 0, [:f 9] 0, [:f 10] 0, [:g 1] 0, [:g 2] 0, [:g 3] 0, [:g 4] 0, [:g 5] 0, [:g 6] 0, [:g 7] 0, [:g 8] 0, [:g 9] 0, [:g 10] 0, [:h 1] 0, [:h 2] 0, [:h 3] 0, [:h 4] 0, [:h 5] 0, [:h 6] 0, [:h 7] 0, [:h 8] 0, [:h 9] 0, [:h 10] 0, [:i 1] 0, [:i 2] 0, [:i 3] 0, [:i 4] 0, [:i 5] 0, [:i 6] 0, [:i 7] 0, [:i 8] 0, [:i 9] 0, [:i 10] 0, [:j 1] 0, [:j 2] 0, [:j 3] 0, [:j 4] 0, [:j 5] 0, [:j 6] 0, [:j 7] 0, [:j 8] 0, [:j 9] 0, [:j 10] 0}
