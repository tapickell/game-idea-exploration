(ns battleship.innout
  "
            ..                                                        
         .+----===---:.                                               
         :+..........::-===----::.                                    
          :+:..................::--===----:..        :+.              
            ==............................::--==----:+=+-             
             :+:......=*----=====---:::..............:..-+.           
               =-......==         ...::-----======--::..:=+           
                :+:.....-+                         .=+-=-             
                  +=-----++                         --.               
    :--. ---    --:    -==-   ---       .----.   :--.  --- -------    
    =##:.###*: .##=    =###=  +##     :*##**##*: =##:  ##* +*###*+    
    =##:.#####=:##=:++:=####*:+## +++ *#*.  .*##.=##:  ##*   ###      
    =##:.##+:*#*##=.==.=##-+##*## -== ##*.   *##.=##: .##*   ###      
    =##:.##+  +###=    =##: :*###     :*##++##*- :*#*+*##-   ###      
    :==. ==-   :==:    :==.  .===       :-==-:     -===-.    ===      
                          . =*=--==   :++- +.:+ =++. ===. ++= ++=     
                         .-- :+:..-+  -#** *:=* +*#.-*-** **- #**     
                               ====+= .==- :--. -:-- :--. -=- =:-: "
  )

(def col-header '(1 2 3 4 5 6 7 8 9 10 "\n"))
(def row-letters ["a:" "b:" "c:" "d:" "e:" "f:" "g:" "h:" "i:" "j:"])

(defn add-row-header [rows]
  (map-indexed (fn [i row]
                 (cons (get row-letters i) row)) rows))

(defn base2str [bi]
  (format "%0100d" (biginteger (.toString (.toBigInteger bi) 2))))

(defn format-board
  "format-board field -> String
  DONE - this should format a field for cli output
    1 2 3 4 5 6 7 8 9 10\n
  a:0 0 0 0 0 0 1 0 0 0\n
  b:0 0 0 0 1 0 0 0 0 0\n..."
  [board]
  (->> board
       (base2str)
       (partition 10)
       (add-row-header)
       (interpose "\n")
       (cons col-header)
       (flatten)
       (apply str)))

(defn get-input [prompt]
  (println prompt)
  (read-line))

(defn get-board-input [prompt board]
  (print board)
  (get-input prompt))

(defn get-user-input
  "DONE - For bot user just make a random move that has not been made
   For a human user get a coord from cli / repl "
  [landings gen-fn]
  (if-let [target (gen-fn (base2str landings))]
    target
    (get-board-input "Enter target in column row format (c 6)\n>" (format-board landings))))
