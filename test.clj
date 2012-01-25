(ns pitch.test
  (:use [overtone.live]
        [overtone.util.log]
        [overtone.gui.info]
        [pitch.core]))

(definst beep [note 60]
  (let [src (sin-osc (midicps note))
        env (env-gen (perc 0.1 0.2) :action FREE)]
    (* src env)))

(beep 86)

(def current-notes (ref #{}))

(defn add-to-current-notes
  [new-note]
  (dosync (ref-set current-notes (set (cons new-note @current-notes)))))

(defn remove-from-current-notes
  [old-note]
  (dosync (ref-set current-notes (set (disj @current-notes old-note)))))

(def kb (midi-in "nanoKEY"))

(defn midi-player [event ts]
  (do (info event)
      (cond
       (= :note-on (:cmd event))
       (do (beep (:note event))
           (add-to-current-notes (:note event)))
       (= :note-off (:cmd event))
       (remove-from-current-notes (:note event)))
      (info "chord "
            (find-chord @current-notes))))

(midi-handle-events kb #'midi-player)

