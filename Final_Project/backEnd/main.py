from google.appengine.ext import ndb
from datetime import datetime
import ast
import webapp2
import json


class Measurement(ndb.Model):
	id = ndb.StringProperty()
	measurement_date = ndb.StringProperty()
	neck_circ = ndb.FloatProperty()
	chest_circ = ndb.FloatProperty()
	upper_arm_circ = ndb.FloatProperty()
	fore_arm_circ = ndb.FloatProperty()
	waist_circ = ndb.FloatProperty()
	hip_circ = ndb.FloatProperty()
	thigh_circ = ndb.FloatProperty()
	calf_circ = ndb.FloatProperty()

class Pinches(ndb.Model):
	id = ndb.StringProperty() 
	body_fat_measure = ndb.FloatProperty()
	body_density_measure = ndb.FloatProperty()
	pinch_test_date = ndb.StringProperty()
	number = ndb.IntegerProperty()
	chest_pinch = ndb.FloatProperty()
	bicep_pinch = ndb.FloatProperty()
	tricep_pinch = ndb.FloatProperty()
	abdominal_pinch = ndb.FloatProperty()
	thigh_pinch = ndb.FloatProperty()
	suprailiac_pinch = ndb.FloatProperty()
	subscapular_pinch = ndb.FloatProperty()
	midaxillary_pinch = ndb.FloatProperty()
	lower_back_pinch = ndb.FloatProperty()
	supraspinale_pinch = ndb.FloatProperty()

class User(ndb.Model):
	id = ndb.StringProperty()
	first_name = ndb.StringProperty()
	last_name = ndb.StringProperty()
	email = ndb.StringProperty()
	male = ndb.BooleanProperty()
	age = ndb.IntegerProperty()
	height = ndb.IntegerProperty()
	weight = ndb.FloatProperty(repeated=True)
	measurements = ndb.StructuredProperty(Measurement, repeated=True)
	pinches = ndb.StructuredProperty(Pinches, repeated=True)

class Measurement_Handler(webapp2.RequestHandler):
	#add new set of measurments
	def post(self, user_id):
		measurement_data = ast.literal_eval(self.request.body)
		if user_id:
			users = User.query()
			for user in users:
				if user.id == user_id:
					curr_user = user
					current_date = datetime.now().date().strftime('%m/%d/%Y')
					new_measurement = Measurement(measurement_date=current_date)
					if measurement_data.get('neck_circ'):
						new_measurement.neck_circ = float(measurement_data['neck_circ'])
					if measurement_data.get('chest_circ'):
						new_measurement.chest_circ = float(measurement_data['chest_circ'])
					if measurement_data.get('upper_arm_circ'):
						new_measurement.upper_arm_circ = float(measurement_data['upper_arm_circ'])
					if measurement_data.get('fore_arm_circ'):
						new_measurement.fore_arm_circ = float(measurement_data['fore_arm_circ'])
					if measurement_data.get('waist_circ'):
						new_measurement.waist_circ = float(measurement_data['waist_circ'])
					if measurement_data.get('hip_circ'):
						new_measurement.hip_circ = float(measurement_data['hip_circ'])
					if measurement_data.get('thigh_circ'):
						new_measurement.thigh_circ = float(measurement_data['thigh_circ'])
					if measurement_data.get('calf_circ'):
						new_measurement.calf_circ = float(measurement_data['calf_circ'])
					new_measurement.put()
					curr_user.measurements.append(new_measurement)
					curr_user.put()
					user_dict = user.to_dict()
					self.response.write(json.dumps(user_dict))
		
			
	
	def get(self, user_id=None):
		if user_id:
			users = User.query()
			for user in users:
				if user.id == user_id:
					curr_user = user
					curr_user_measurements = curr_user.measurements[0].to_dict()
					self.response.write(json.dumps(curr_user_measurements))


class PinchTest_Handler(webapp2.RequestHandler):
	#add new set of pinches for 4 pinch test
	def post(self, user_id):
		pinch_data = ast.literal_eval(self.request.body)
		if user_id:
			users = User.query()
			for user in users:
				if user.id == user_id:
					curr_user = user
			current_date = datetime.now().date().strftime('%m/%d/%Y')
			new_pinches = Pinches(pinch_test_date=current_date)
			new_pinches.bicep_pinch = float(pinch_data['bicep'])
			new_pinches.tricep_pinch = float(pinch_data['tricep'])
			new_pinches.subscapular_pinch = float(pinch_data['subscapular'])
			new_pinches.suprailiac_pinch = float(pinch_data['suprailiac'])
			new_pinches.put()
			curr_user.pinches.append(new_pinches)
			curr_user.put()
			curr_user_dict = curr_user.to_dict()
			self.response.write(json.dumps(curr_user_dict))

	def get(self, user_id):
		if user_id:
			users = User.query()
			for user in users:
				if user.id == user_id:
					curr_user = user
					curr_user_pinches = curr_user.pinches[0].to_dict()
					self.response.write(json.dumps(curr_user_pinches))

#test class
class MainPage(webapp2.RequestHandler):
	def post(self):
		user_data = ast.literal_eval(self.request.body)
		new_user = User()
		users = User.query()
		for user in users:
			if user.id == user_data['user']:
				self.response.write("User already exists")
				return
		if user_data.get('gender') == 'male':
			new_user.male = True
		else:
			new_user.male = False
		new_user.first_name = user_data['first_name']
		new_user.last_name = user_data['last_name']
		new_user.email = user_data['email']
		new_user.id = user_data['user']
		new_user.put()
		new_user_dict = new_user.to_dict()
		self.response.write(json.dumps(new_user_dict))



class UserHandler(webapp2.RequestHandler):
	def put(self):
		user_data = ast.literal_eval(self.request.body)
		user_id = user_data['user']
		users = User.query()
		for user in users:
			if user.id == user_id:
				#self.response.write("User Found!")
				new_user = user
				if user_data.get('gender') == 'male':
					new_user.male = True
				else:
					new_user.male = False
				new_user.first_name = user_data['first_name']
				new_user.last_name = user_data['last_name']
				new_user.email = user_data['email']
				new_user.age = int(user_data['age'])
				new_user.height = int(user_data['height'])
				new_user.id = user_data['user']
				weight = int(user_data['weight'])
				new_user.put()
				self.response.write(json.dumps(new_user.to_dict()))


	def get(self, user_id):
		if user_id:
			users = User.query()
			for user in users:
				if user.id == user_id:
					curr_user = user
					self.response.write(json.dumps(curr_user.to_dict()))

allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods

#url routing
app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/measurements', Measurement_Handler),
    ('/pinchtest', PinchTest_Handler),
    ('/measurements/(.*)', Measurement_Handler),
    ('/pinchtest/(.*)', PinchTest_Handler),
    ('/user', UserHandler),
    ('/user/(.*)', UserHandler)
], debug=True)