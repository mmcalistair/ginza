(ns ginza.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ginza.state :as state]
            [ginza.oauth :as oauth]
            [hiccup.core :refer :all]
            [ginza.office365api :as office365]))

(defroutes app-routes
  (GET "/" []
    (html [:html
             [:head
              [:title "Ginza"]
              [:link {:rel "stylesheet" :type "text/css" :href "main.css"}]
              [:link {:href "https://fonts.googleapis.com/css?family=Inconsolata"
                      :rel "stylesheet"}]]
             [:body
                [:div#header
                   [:h1 "GINZA"]]
                [:div#spacer]
                [:div#mainwrapper
                   [:div#main
                      [:table
                         [:tr
                            [:td
                               [:p "Connection Status"]]
                            [:td
                               (if (true? (state/is-connected?))
                                   [:p.green "connected"]
                                   [:p.red "not connected"])]]]
                      [:a {:href oauth/req-token-link} "Reconnect"]]]]]))
  (GET "/oauth" request
    (if (contains? (:params request) :code)
      (oauth/request-access-token (:code (:params request)))
      "No valid code returned"))

  (GET "/test.xml" []
    (office365/api-get-contacts))
  (GET "/test2.xml" []
           {:status 200
            :headers {"Content-Type" "text/xml;charset=UTF-8"}
            :body "<?xml version=\"1.0\" encoding=\"UTF-8\"?><YealinkIPPhoneBook><Title>Yealink</Title><Menu Name=\"Default\"><Unit Name=\"Weber Dennis\"  Phone1=\"015112727388\" /><Unit Name=\"Purmann Ronny\"  Phone1=\"070728919003\" /><Unit Name=\"Trenkle Simon\"  Phone1=\"074238090823\"  Phone2=\"015771320842\" /><Unit Name=\"Kirsch Wolfgang\"  Phone1=\"0791509–326\" /><Unit Name=\"Schur Marcus\"  Phone1=\"01722515261\" /><Unit Name=\"Braunstein Elisabeth\"  Phone1=\"074238090824\" /><Unit Name=\" \"  Phone1=\"07721 22031\" /><Unit Name=\"Schmidtberger Adrian\"  Phone1=\"01747393304\" /><Unit Name=\"Wende Grischa\"  Phone1=\"070728919006\" /><Unit Name=\"Bippus Sascha\"  Phone1=\"070728919004\"  Phone2=\"14\" /><Unit Name=\"Purmann Ronny\"  Phone1=\"070728919003\"  Phone2=\"13\" /><Unit Name=\"Lang Dietmar\"  Phone1=\"11\"  Phone2=\"01705401576\" /><Unit Name=\"Schnitzler Stefan\"  Phone1=\"0711806089949\"  Phone2=\"071180608919\" /><Unit Name=\"Lang Sabine\"  Phone1=\"070728919002\"  Phone2=\"12\" /><Unit Name=\"Samsel Thomas\"  Phone1=\"061515002412\" /><Unit Name=\"Vertrieb J&amp;T\"  Phone1=\"07071968785\" /><Unit Name=\"Maucher Christian\"  Phone1=\"015117167667\" /><Unit Name=\"Koch Michael\"  Phone1=\"070728919000\" /><Unit Name=\"Deiber Benjamin\"  Phone1=\"15\"  Phone2=\"070728919005\"  Phone3=\"070728919005\"  Phone4=\"016097548040\" /><Unit Name=\"Cantepe Koray\"  Phone1=\"071819643100\" /><Unit Name=\"Schnitzler \"  Phone1=\"0711806089949\" /><Unit Name=\"Cantepe Koray\"  Phone1=\"071819643100\" /><Unit Name=\"Scherer Andreas\"  Phone1=\"074238090821\"  Phone2=\"001799109793\" /><Unit Name=\"Ulrich Dirk\"  Phone1=\"0934223815\"  Phone2=\"016097965633\" /><Unit Name=\"Pfau Philipp\"  Phone1=\"074238090822\"  Phone2=\"015231079657\" /><Unit Name=\"Lang Sabine\"  Phone1=\"070728919002\" /><Unit Name=\"Lang Dietmar\"  Phone1=\"070728919001\" /><Unit Name=\"Pfau Philipp\"  Phone1=\"074238090822\"  Phone2=\"015231079657\" /><Unit Name=\"Leonhardt Steffen\"  Phone1=\"01637118091\" /><Unit Name=\"Scherer Andreas\"  Phone1=\"074238090821\"  Phone2=\"001799109793\" /><Unit Name=\"Grimm Peggy\"  Phone1=\"0934285600\" /><Unit Name=\"Schnitzler Stefan\"  Phone1=\"0711806089949\"  Phone2=\"071180608919\"  Phone3=\"01637118069\" /><Unit Name=\"Trenkle Simon\"  Phone1=\"074238090823\"  Phone2=\"01711760414\" /><Unit Name=\"Mueller Toni\"  Phone1=\"061515002285\" /></Menu></YealinkIPPhoneBook>"})
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
