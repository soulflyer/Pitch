(ns pitch.core
  (:use [overtone.live]))

(defn list-defined-scales
  []
  (doseq [keyval SCALE]
    (prn (key keyval)
         (val keyval))))

(def testscale [2 2 2 1 2 2 1])

(defn find-name
  "Returnd the name of the first matching thing found in things
  or nil if not found"

  ([thing things]
     (if (= (val (first things)) thing)
       (key (first things))
       (if (< 1 (count things))
         (find-name thing (rest things))))))

(defn find-scale-name
  "Return the name of the first matching scale found in SCALE
  or nil if not found

  ie: (find-scale-name [2 1 2 2 2 2 1]
  :melodic-minor-asc"

  [scale]
  (find-name scale SCALE))

;; (defn- find-normalised-chord-name
;;   "Return the name of the first matching chord in CHORD
;;   or nil if not found
;;   chord must contain 0

;;   ie: (find-normalised-chord-name #{0 3 7}
;;   :minor"

;;   [chord]
;;   (find-name chord CHORD))

(defn find-note-name

  [note]
  (REVERSE-NOTES (mod note 12)))

(defn- fold-note
  "Folds note intervals into a 2 octave range so that chords using notes
  spread across multiple octaves can be correctly recognised."

  [note]
  (if (or (< 21 note) (contains? #{20 19 16 12} note))
    (fold-note (- note 12))
     note ))

(defn- simplify-chord
  "expects notes to contain 0 (the root note) Reduces all notes into 2 octaves. This will allow
  identification of fancy jazz chords, but will miss some simple chords if they are spread over
  more than 1 octave."

  [notes]
  (set (map (fn [x] (fold-note x)) notes)))

(defn- compress-chord
  "expects notes to contain 0 (the root note) Reduces all notes into 1 octave. This will lose
  all the fancy jazz chords but recognise sparse multiple octave smple chords"

  [notes]
  (set (map (fn [x] (mod x 12)) notes)))

(defn- select-root
  "Adds a new root note below the lowest note present in notes"
  [notes root-index]
  (if (< 0 root-index)
    (let [new-root (nth (seq (sort notes)) root-index)
         lowest-note (first (sort notes))
         octaves (+ 1 (quot (- new-root lowest-note) 12))]
      (set (cons (- new-root (* octaves 12)) notes)))
    notes))

(defn- find-chord-with-low-root
  "Finds the chord represented by notes
   Assumes the root note is the lowest note in notes
   notes can be spread over multiple octaves"

  [notes]
  (if (< 0 (count notes))
    (let [root (first (sort notes))
          adjusted-notes (set (map (fn [x] (- x root)) notes ))]
      (or (find-name (simplify-chord adjusted-notes) CHORD)
          (find-name (compress-chord adjusted-notes) CHORD)))))

(defn find-chord
  [notes]
  (loop [note 0]
    (if (< note (count notes) )
      (let [mod-notes (select-root notes note)
            chord  (find-chord-with-low-root mod-notes)
            root (find-note-name (first (sort mod-notes)))]
       (if chord
         {:root root :chord-type chord}
         (recur (inc note))))
      nil)))




























































