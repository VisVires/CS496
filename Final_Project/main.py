from google.appengine.ext import ndb
from datetime import datetime
import webapp2
import json

class User:
	id = ndb.StringProperty(required=True)
	first_name = ndb.StringProperty()
	last_name = ndb.StringProperty()
	email = ndb.StringProperty()
	male = ndb.BooleanProperty()

class Measurements:
	measurement_date = ndb.StringProperty(required=True)
	height = ndb.IntegerProperty()
	weight = ndb.FloatProperty()
	neck_circ = ndb.FloatProperty()
	chest_circ = ndb.FloatProperty()
	upper_arm_circ = ndb.FloatProperty()
	fore_arm_circ = ndb.FloatProperty()
	waist_circ = ndb.FloatProperty()
	hip_circ = ndb.FloatProperty()
	thigh_circ = ndb.FloatProperty()
	calf_circ = ndb.FloatProperty()
	body_fat_measure = ndb.FloatProperty()
	body_density_measure = ndb.FloatProperty()

class Pinches:
	pinch_test_date = ndb.StringProperty(required=True)
	number = ndb.IntegerProperty(required=True)
	chest_pinch = ndb.FloatProperty()
	bicep_pinch = ndb.FloatProperty()
	tricep_pinch = ndb.FloatProperty()
	abdominal_pinch = ndb.FloatProperty()
	thigh_pinch = ndb.FloatProperty()
	suprailiac_pinch = ndb.FloatProperty()
	supraclavicular_pinch = ndb.FloatProperty()
	midaxillary_pinch = ndb.FloatProperty()
	lower_back_pinch = ndb.FloatProperty()
	supraspinale_pinch = ndb.FloatProperty()


class Measurement_Handler(webapp2.RequestHandler):

class PinchTest_Handler(webapp2.RequestHandler):


#test class
class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.write('Hello, this is just a test page3')

#create patch method
allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods

#url routing
app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/measurements', Measurement_Handler),
    ('/pinchtest', PinchTest_Handler)
], debug=True)