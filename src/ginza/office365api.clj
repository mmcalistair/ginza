(ns ginza.office365api
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [ginza.state :as state]
            [clojure.string :as str]))

(defn get-number-list [in]
  (if (not (empty? in))
    (loop [lst in
           res '()]
      (if (empty? lst)
        res
        (let [curItem (first in)]
          (recur (rest lst)
                 (conj res curItem)))))
    nil))


(defn append-numbers [lst app]
  (if (not (empty? app))
    (conj lst (flatten app))
    lst))

(defn reformat-nr [lst]
  (map (fn [x]
         (let [nr (str/replace x "+49" "0")
               nr (str/replace nr "-" "")
               nr (str/replace nr " " "")
               nr (str/replace nr "/" "")]
           nr)) lst))

(defn reformat-phone-numbers [recipients]
  (loop [recipients recipients
         res '()]
    (if (empty? recipients)
      res
      (recur (rest recipients)
             (conj res {:name (:name (first recipients))
                        :phones (reformat-nr (:phones (first recipients)))})))))

(defn format-xml-phones [phone-list]
  (println "Phones" phone-list)
  (loop [phones phone-list
         cnt 1
         res '()]
    (if (empty? phones)
      res
      (recur (rest phones)
             (inc cnt)
             (conj res (str "Phone" cnt "=\"" (first phones) "\" "))))))

(defn format-single-entry [recp]
  (let [prefix "<Unit "
        suffix "/>"
        name (str "Name=\"" (:name recp) "\" ")
        phones (str/join " " (format-xml-phones (:phones recp)))]
    (str prefix name phones suffix)))


(defn format-to-xml [entries]
  (let [header "<?xml version=\"1.0\" encoding=\"UTF-8\"?><YealinkIPPhoneBook>\n<Title>Yealink</Title>\n<Menu Name=\"Default\">"
        footer "</Menu>\n</YealinkIPPhoneBook>"
        item (map format-single-entry entries)]
    (println "")
    (str header (str/join item) footer)))



(defn parse-res [json-data]
  (loop [data json-data
         res '()]
    (if (empty? data)
      res
      (let [curItem (first data)]
        (let [name (:displayName curItem)
              mobileList (:mobilePhone curItem)
              businessList (get-number-list (:businessPhones curItem))
              homeList (get-number-list (:homePhones curItem))
              nrs (if (not (nil? mobileList))
                    (conj '() mobileList)
                    '())
              nrs (append-numbers nrs businessList)
              nrs (flatten (append-numbers nrs homeList))]
          (println name nrs)
          (recur (rest data)
                 (conj res {:name name :phones nrs})))))))

(defn api-get-contacts []
  (println (:access_token (state/get-token)))
  (let [res (client/get "https://graph.microsoft.com/v1.0/me/contacts"
              {:headers
                 {:Authorization (:access_token (state/get-token))
                  :Content-Type "application/json"}})]
    (println (str (:body res)))
    (let [data (:value (json/read-json (:body res) true))]
      (let [parsed (parse-res data)]
        (let [parsed (reformat-phone-numbers parsed)]
          (let [parsed (format-to-xml parsed)]
            {:status 200
             :headers {"Content-Type" "text/xml;charset=UTF-8"}
             :body (str parsed)}))))))

