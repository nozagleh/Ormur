# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import json

from django.shortcuts import render
from django.http import JsonResponse, HttpRequest, HttpResponse
from django.core import serializers

from drinks.models import Drink
from users.models import User

def getAllJSON(request, key):
	if not User.objects.filter(key=key) :
		return HttpResponse(status=404)

	user = User.objects.get(key=key)

	if not Drink.objects.filter(user=user) :
		return HttpResponse(status=404)

	dJson = {'type': 'drink','drinks':[]}
	drinks = list(Drink.objects.all().filter(user=user.id))
	print drinks
	for drink in drinks :
		dJson['drinks'].append(drink.getJSON())
	
	return JsonResponse(dJson)
	#return JsonResponse({'drinks':[{'drink':{'title':'arnar','rating':2.5,'description':'lorem ipsum', 'location':'locationLocation'}},{'drink':{'title':'bob','rating':2.2,'description':'Testing description', 'location':'loc1,loc2'}}]})


# Create your views here.
def getDrinks(self, request):
	return
	#return JsonResponse({'drinks':[{'drink':{'title':'arnar','rating':2.5,'description':'lorem ipsum', 'location':'locationLocation'}},{'drink':{'title':'bob','rating':2.2,'description':'Testing description', 'location':'loc1,loc2'}}]})
