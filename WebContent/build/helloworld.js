"use strict";

React.render(React.createElement(
  "h1",
  null,
  " REACT: Welcome to this chatting room !!"
), document.getElementById('example'));
React.render(React.createElement(
  "ul",
  { id: "toggleVisible", "class": "actions" },
  React.createElement(
    "li",
    null,
    " ",
    React.createElement("input", { type: "text", id: "usernameText", placeholder: "username..." }),
    " "
  ),
  React.createElement(
    "li",
    null,
    React.createElement("input", { type: "button", "class": "button", value: "Change Username", onClick: "changeUsername()" }),
    " "
  )
), document.getElementById('try'));