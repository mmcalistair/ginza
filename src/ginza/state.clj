(ns ginza.state)

(def state (atom :not-connected))

(defn is-connected? []
  (if (= @state :connected)
    true
    false))

(defn set-status-connected []
  (reset! state :connected))

(defn set-status-disconnected []
  (reset! state :not-connected))
