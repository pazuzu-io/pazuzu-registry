'use strict';

const React = require('react');
const client = require('./client');

var HeaderMenu = React.createClass({displayName: 'HeaderMenu',
	render: function() {
		return (
		React.createElement('div', {className: 'headerMenu'}
			
			)
		);
	}
});

var HeaderZalando = React.createClass({displayName: 'HeaderZalando',
	render: function() {
		return (
		React.createElement('div', {className: 'headerZalando'},
			"ZALANDO"
			)
		);
	}
});

var Header = React.createClass({displayName: 'Header',
	render: function() {
		return (
		<div className="header">
			<HeaderMenu />
			Pazuzu - The Docker Maker
			<HeaderZalando />
		</div>
		);
	}
});

React.render(
	React.createElement(Header, null),
	document.getElementById('react')
)



