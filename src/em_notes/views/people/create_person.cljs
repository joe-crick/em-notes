(ns em-notes.views.people.create-person
  (:require
   [re-frame.core :as rf]
   [em-notes.lib.get-state :refer [get-state]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.db :refer [default-db]]
   [em-notes.i18n.tr :refer [grab]]))

(defn create-person []
  ;; setup local state
  
  (let [[person revise!] (get-state (:person default-db))]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:section
       [:div.container
        [:div
         [:h1 {:class "title mt-5"}
          (grab :person/title)]

         [:form
          [:fieldset
           [:legend (grab :person/details)]
           [:div.field
            [:label  (grab :person/first-name)]
            [:p.control
             [:input.input {:html-for :first-name
                            :type "text"
                            :id :first-name,
                            :value (:first-name @person)
                            :on-change #(revise! :first-name %)}]]]
           [:div.field
            [:label (grab :person/last-name)]
            [:p.control
             [:input.input {:html-for :last-name
                            :type "text"
                            :id :last-name,
                            :value (:last-name @person)
                            :on-change #(revise! :last-name %)}]]]
           [:div.field
            [:label (grab :person/team)]
            [:p.control
             [:input.input {:html-for :team
                            :type "text",
                            :id :team
                            :value (:team @person)
                            :on-change #(revise! :team %)}]]]]
          [:hr]
          [:fieldset
           [:legend (grab :person/feedback)]
           [:div.field
            [:label (grab :person/feedback-medium)]
            [:p.control
             [:input.input {:html-for :feedback-medium
                            :type "text"
                            :id :feedback-medium,
                            :value (-> @person :feedback :feedback-medium)
                            ;; need to be able to update-in
                            :on-change #(revise! [:feedback :feedback-medium] %)}]]]
           [:div.field
            [:label (grab :person/when-receive-feedback)]
            [:p.control
             [:input.input {:html-for :when-receive-feedback
                            :type "text"
                            :id :when-receive-feedback,
                            :value (-> @person :feedback :when-receive-feedback)
                            :on-change #(revise! [:feedback :when-receive-feedback] %)}]]]
           [:div.field
            [:label (grab :person/how-receive-recognition)]
            [:p.control
             [:input.input {:html-for :how-receive-recognition
                            :type "text"
                            :id :how-receive-recognition,
                            :value (-> @person :feedback :how-receive-recognition)
                            :on-change #(revise! [:feedback :how-receive-recognition] %)}]]]

           [:div.field
            [:label (grab :person/what-rewards-wanted)]
            [:p.control
             [:input.input {:html-for :what-rewards-wanted
                            :type "text"
                            :id :what-rewards-wanted,
                            :value (-> @person :feedback :what-rewards-wanted)
                            :on-change #(revise! :what-rewards-wanted %)}]]]]
          [:hr]
          [:fieldset
           [:legend (grab :person/mood)]
           [:div.field
            [:label (grab :person/what-makes-grumpy)]
            [:p.control
             [:input.input {:html-for :what-makes-grumpy
                            :type "text"
                            :id :what-makes-grumpy,
                            :value (-> @person :mood :what-makes-grumpy)
                            :on-change #(revise! [:mood :what-makes-grumpy] %)}]]]
           [:div.field
            [:label (grab :person/how-to-know-grumpy)]
            [:p.control
             [:input.input {:html-for :how-to-know-grumpy
                            :type "text"
                            :id :how-to-know-grumpy,
                            :value (-> @person :mood :how-to-know-grumpy)
                            :on-change #(revise! [:mood :how-to-know-grumpy] %)}]]]
           [:div.field
            [:label (grab :person/how-to-help-grumpy)]
            [:p.control
             [:input.input {:html-for :how-to-help-grumpy
                            :type "text"
                            :id :how-to-help-grumpy,
                            :value (-> @person :mood :how-to-help-grumpy)
                            :on-change #(revise! [:mood :how-to-help-grumpy] %)}]]]
           ]]

         [form-footer #(rf/dispatch [:create-person @person])]]]])))



