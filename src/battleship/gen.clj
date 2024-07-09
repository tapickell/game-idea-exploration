(ns battleship.gen
  (:require [battleship.convert :as cvt]
            [battleship.ai :as ai]
            [battleship.innout :as inNout]))

(defn user-type [in]
  (if (re-matches #"[yY]" in) :human :pc))

(def empty-field
  "This is an 8 bit zero to start from."
  0N)

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

(defn create-and-place-ships
  "DONE - Randomly place ships on field, could use pre-generated fields"
  [field]
  (starter-field-gen ships field))

(defn create-player [name human?]
  {:name name :type (user-type human?)})

(defn create-board
  "Create a board for the player passed in.
   This will have ships randomly placed without collision."
  [player]
  {:board (create-and-place-ships empty-field) :landings empty-field :player player})

