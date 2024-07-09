(ns battleship.convert)
(require '[clojure.string :as str])

(defn places-to-left [n]
  (loop [x (bigint 1) counter 1]
    (if (= counter n)
      x
      (recur (+ x x) (+ 1 counter)))))

(defn landings->hits [board landings]
  (bit-and board landings))

(defn landings->misses [board landings]
  (- landings (landings->hits board landings)))

(defn input->coord
  "input->coord \"a 10\" -> [:a 10]
  Convert a valid user input string to a vector of coordinates.
  Invalid input throws an AssertionError"
  [input]
  {:pre [(some? (re-matches #"^[a-j]\s[1-9][0]?$" input))]}
  (let  [[r c] (str/split input #" ")
         row (keyword r)
         column (Integer/parseInt c)]
    [row column]))

(def row-m {:a 90 :b 80 :c 70 :d 60 :e 50 :f 40 :g 30 :h 20 :i 10 :j 0})
(def col-numbs {1 10, 2 9, 3 8, 4 7, 5 6, 6 5, 7 4, 8 3, 9 2, 10 1})

(defn coord->target
  "coord->target [:a 8] -> 2r00100000.....
  Convert a valid coordinate vector into a field with the coordinate set to 1.
  DONE - implement this to do something like [:a 8] -> 2r00000001"
  [row column]
  (places-to-left (+
                   (row row-m)
                   (get col-numbs column))))

(coord->target :j 1)
;; => 512N
(coord->target :j 10)
;; => 1N

(def row-index {0 :a 10 :b 20 :c 30 :d 40 :e 50 :f 60 :g 70 :h 80 :i 90 :j})

(defn index->coord
  "Takes a 0-99 index and creates the proper [:j 7] vector coordinate.
   Throws an AssertionError if index is out of range."
  [index]
  {:pre [(< -1 index 100)]}
  (let [tens (* 10 (int (/ index 10)))
        row (get row-index tens)
        ones (+ 1 (int (mod index 10)))]
    [row ones]))


;; pass
(re-matches #"^[a-j]\s[1-9][0]?$" "a 1")
(re-matches #"^[a-j]\s[1-9][0]?$" "a 10")
(re-matches #"^[a-j]\s[1-9][0]?$" "j 1")

;; fail
(re-matches #"^[a-j]\s[1-9][0]?$" "a 0")
(re-matches #"^[a-j]\s[1-9][0]?$" "a 11")
(re-matches #"^[a-j]\s[1-9][0]?$" "k 10")
