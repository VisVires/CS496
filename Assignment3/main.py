from google.appengine.ext import ndb
import webapp2
import json

#define boat class
class Boat(ndb.Model):
	id = ndb.StringProperty()
	name = ndb.StringProperty(required=True)
	type = ndb.StringProperty()
	length = ndb.IntegerProperty()
	at_sea = ndb.BooleanProperty()


#manage boat verbs
class BoatHandler(webapp2.RequestHandler):
	#add new boat
	def post(self):
		boat_data = json.loads(self.request.body)
		new_boat = Boat(name=boat_data['name'], at_sea=True)
		new_boat.put()
		boat_dict = new_boat.to_dict()
		boat_dict['self'] = '/boat/' + new_boat.key.urlsafe()
		self.response.write(json.dumps(boat_dict))

	#get boat info
	def get(self, id=None):
		if id:
			curr_boat = ndb.Key(urlsafe=id).get()
			curr_boat_dict = curr_boat.to_dict()
			curr_boat_dict['self'] = '/boat/' + id
			self.response.write(json.dumps(curr_boat_dict))
			self.response.write(curr_boat_dict['name'])

	#delete boat info
	def delete(self, id=None):
		if id:
			del_boat = ndb.Key(urlsafe=id).get()
			del_boat.key.delete()
			self.response.write('Deleted Boat')

#define slip class
class Slip(ndb.Model):
	id = ndb.StringProperty()
	number = ndb.IntegerProperty(required=True)
	current_boat = ndb.StringProperty()
	arrival_date =  ndb.DateProperty()
	departure_history = ndb.JsonProperty()


#manage the slip verbs
class SlipHandler(webapp2.RequestHandler):
	#add new slip
	def post(self):
		slip_data = json.loads(self.request.body)
		new_slip = Slip(number=slip_data['number'], current_boat='empty')
		new_slip.put()
		slip_dict = new_slip.to_dict()
		slip_dict['self'] = '/slip/' + new_slip.key.urlsafe()
		self.response.write(json.dumps(slip_dict))

	#get slip info
	def get(self, id=None):
		if id:
			curr_slip = ndb.Key(urlsafe=id).get()
			curr_slip_dict = curr_slip.to_dict()
			curr_slip_dict['self'] = '/slip/' + id
			self.response.write(json.dumps(curr_slip_dict))

	#delete boat at id
	def delete(self, id=None):
		if id:
			del_slip = ndb.Key(urlsafe=id).get()
			del_slip.key.delete()
			self.response.write('Deleted Slip')

	#def put(self, id=None):


	#def patch(self, id):

class BoatListHandler(webapp2.RequestHandler):
	def get(self):
		boat_list = Boat.query()
		boat_list_fetch = boat_list.fetch()
		for item in boat_list_fetch:
			self.response.write(item)
			self.response.write('\n')

#test class
class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.write('Hello')


allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods


app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/boat', BoatHandler),
    ('/boats', BoatListHandler),
    ('/boat/(.*)', BoatHandler),
    ('/slip', SlipHandler),
    ('/slip/(.*)', SlipHandler)
], debug=True)