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

(defn- add-row-header [rows]
  (map-indexed (fn [i row]
                 (cons (get row-letters i) row)) rows))

(defn base2str
  "base2str BigInt -> String
  Returns a binary (2r) string representation of the BigInt left padded 100 places"
  [bi]
  (format "%0100d" (biginteger (.toString (.toBigInteger bi) 2))))

(defn format-board
  "format-board BigInt -> String
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

(defn get-input
  "get-input String -> String
  Prints the prompt string and returns user input from read-line"
  [prompt]
  (println prompt)
  (read-line))

(defn- get-board-input [prompt board]
  (print board)
  (get-input prompt))

(defn get-user-input
  "get-user-input BigInt (fn String -> BigInt | nil) -> BigInt
  Runs the gen-fn and reutrns output if it succeeds
  if not it gathers coordinate input from the user."
  [landings gen-fn]
  (if-let [target (gen-fn (base2str landings))]
    target
    (get-board-input "Enter target in column row format (c 6)\n>" (format-board landings))))
