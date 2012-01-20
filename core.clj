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

(defn find-chord-name
  "Return the name of the first matching chord in CHORD
  or nil if not found

  ie: (find-chord-name #{0 3 7}
  :minor"

  [chord]
  (find-name chord CHORD))

(defn find-note-name
  "Return name of the first matching note in NOTES
  or nil if not found

  ie: (find-note-name 3)
  :D#"

  [note]
  (find-name note NOTES))

(defn fold-note
  "Folds note intervals into a 2 octave range so that chords using notes
  spread across multiple octaves can be correctly recognised."

  [note]
  (if (or (< 21 note) (contains? #{20 19 16 12} note))
    (fold-note (- note 12))
     note ))

(defn simplify-chord
  "expects notes to contain 0 (the root note) Reduces all notes into 2 octaves. This will allow
  identification of fancy jazz chords, but will miss some simple chords if they are spread over
  more than 1 octave."

  [notes]
  (set (map (fn [x] (fold-note x)) notes)))

(defn compress-chord
  "expects notes to contain 0 (the root note) Reduces all notes into 1 octave. This will lose
  all the fancy jazz chords but recognise sparse multiple octave smple chords"

  [notes]
  (set (map (fn [x] (mod x 12)) notes)))

(defn deinvert-chord
  "rearranges notes so that the new-root th entry in notes becomes the root"

  [notes new-root]
  {}
  )

(defn find-chord
  "Assumes the root note is the lowest note in notes"
  [notes]
  (if (< 0 (count notes))
    (let [root (first (sort notes))
          adjusted-notes (set (map (fn [x] (- x root)) notes ))]
      {:root (find-note-name (mod root 12))
       :chord-type (or (find-chord-name (simplify-chord adjusted-notes))
                       (find-chord-name (compress-chord adjusted-notes)))})))

