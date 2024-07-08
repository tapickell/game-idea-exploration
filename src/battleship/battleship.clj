(ns battleship.battleship
  (:gen-class)
  (:require [battleship.convert :as convert])
  (:require [battleship.gen :as gen])
  (:require [battleship.innout :as inNout])
  (:require [battleship.ai :as ai]))

(defn won?
  "Checks to see if the landed hits match the ship placement on the board."
  [board landings]
  (if (and (= 0 board) (= 0 landings))
    false
    (= board (convert/landings->hits board landings))))


(defn launch-projectile
   "Records the projectile landing
    maybe use guard clauses around what a coord is to remove if-"
  [landings [row col]]
  (if-let [target (convert/coord->target row col)]
    (+ landings target)
    landings))

(defn game-loop
  "Checks for win on opponent board,
   since it would have been the last turn taken unless begining of game.
   If no winner gets player target and records it on the players landings.
   Recursively loops for each turn switching the boards back and forth."
  [player-board opponent-board]
  (loop [b1 player-board b2 opponent-board]
    (if (won? (:board player-board) (:landings opponent-board))
      (and (println "Player %s won" (:player-name b2)) 0)
      (let [lnd (:landings b1)
            tg (ai/target-generator (:user-type b1) convert/index->coord)
            in (inNout/get-user-input lnd tg)
            coord (convert/input->coord in)
            b1-updated (launch-projectile b1 coord)]
        (recur b2 b1-updated)))))

(defn setup-game
  "Here is where we get names and player types from user input."
  []
  (let [n1 (inNout/get-input "Enter player1 name:")
        t1 (inNout/get-input "is player1 human? (y/n)")
        n2 (inNout/get-input "enter player2 name:")
        t2 (inNout/get-input "is player2 human? (y/n)")
        p1 (gen/create-player n1 t1)
        p2 (gen/create-player n2 t2)
        player1-board (gen/create-board p1)
        player2-board (gen/create-board p2)]
    [player1-board player2-board]))

;; (setup-game)

(defn test-game []
  (let [[b1 b2] (setup-game)]
    (game-loop b1 b2)))

(defn -main
  "I don't do a whole lot ... yet."
  [& _args]
  (let [[b1 b2] (setup-game)]
    (game-loop b1 b2)))

(comment
  "Things to track:
     Where projectiles land on the board ;; be able to present this in CLI
     1 :miss 0 :not-targeted -1 :hit
     Where ships are placed on board
     1 :ship-in-coor 0 :nothing-in-coor

     Who's turn it is, just was on last loop

     Should a player have a name and a type:
     {:name :player1 :type :human}
     {:name :player2 :type :bot | :ai | :pc}
  8x8   {:a 2r1110010 :b 2r00000000 ....}
  16x16 {:a 2r0000111100001111 :b 2r1111000011110000 ...}

    1110000000010000010000100100011110
  + 0000001000000000000000000000000000
    ----------------------------------
    1110001000010000010000100100011110

    1110000000010000010000100100011110
  & 0000001000000000000000000000000000
    ----------------------------------
    0000001000000000000000000000000000

(+ 2R1100 2r0010)
(let [a 2r11000000]
  a)
(bit-or 2r11000000 2r00100000)

;; Already targeted? same functionality
;; is target landing a hit? Hit -> not "0" Miss -> "0"
;; (landings->hits board target)
(Integer/toString (bit-and 2r11001010 2r00100000) 2)
;; What (landings->hits board landings) same functionality
(Integer/toString (bit-and 2r11001010 2r10101010) 2)
10001010

;; Misses from landings and hits
;; (landings->misses board landings)
(- landings (landings->hits board landings))
(let [board    2r11110000
      landings 2r00100100
      expected 2r00000100
      hits (bit-and board landings)
      misses (- landings hits)]
  (Integer/toString misses 2))

(use 'clojure.data)
(clojure.data/diff [1 0 1 0 1 0 1 0] [0 0 1 0 0 0 1 0])


;; Add to previous landings
;; (add-landing landings target)
(Integer/toString (+ 2r01000000 2r00100000) 2)

;; Won?
;; (= board (landings->hits board landings))
(= 2r1110010001100010 2r1110010001100010)


(bit-and-not 2r11000000 2r00100000)
(bit-and 2r11100000 2r00100000)
(bit-and-not 2r11100000 2r00100000)
(bit-xor 2r11000000 2r00100000)
(bit-flip 2r11000000 2r00100000)
(bit-shift-right 2r11000000 2r00100000)
(* 2r11000000 2r00100000)
(/ 2r11000000 2r00100000)
(- 2r11000000 2r00100000)
(+ 2r11000000 2r00100000)

END COMMENT")
