(ns em-notes.components.form-footer
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]))

(defn form-footer [submit on-cancel]
  (let [cancel-fn (if (nil? on-cancel) identity on-cancel)]
    (fn []
      [:div {:class (css-cls :is-flex :is-justify-content-space-between :mt-5)}
       [:div
        [:button {:class (css-cls :button :is-primary)
                  :type "button"
                  :on-click submit} (grab :form/submit)]]
       [:div
        [:button {:class (css-cls :button :is-info)
                  :type "button"
                  :on-click cancel-fn} (grab :form/cancel)]]])))