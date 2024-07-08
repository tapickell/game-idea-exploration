(ns battleship.ai)

(defn index-notin-vec?
  [index vec]
  (not (some #(= % index) vec)))

(defn find-empty-index [field-str found-fn]
      (loop [tried []]
        (when (> (count tried) (count field-str))
          (throw (Exception. "AI target-generator: Exhausted field with no target available")))
        (let [idx (rand-int 100)
              ele (get field-str idx)]
          (if (and (index-notin-vec? idx tried) (= \0 ele))
            (found-fn idx)
            (recur (conj tried idx))))))

(defn target-generator
  "DONE - Picks a random target that has not been choosen.
  Could take into account hits and try to select a target from a hit point.
  Could also use weighted indexes to select coordinates more likley to hit."
  [user-type convertor]
  (if (= user-type :human)
    (fn [_landings-str] false)
    (fn [landings-str] (find-empty-index landings-str convertor))))

;; pick a random index 0-99
;; check if that index has a 0
;; if so use converter to make it a coordinate vector
;; if not pick another random index until there are no more avail
;; (store indexes tried and count that to make sure we have not tried 100+ times)

