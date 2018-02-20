# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import json

from django.shortcuts import render
from django.http import JsonResponse
from django.core import serializers

from drinks.models import Drink

def getAllJSON(self):
	drinks = serializers.serialize("json",Drink.objects.all(), fields=('title','rating','description','location'))
	dJson = {'drinks':[]}
	for drink in Drink.objects.all() :
		dJson['drinks'].append(drink.getJSON())
	
	return JsonResponse(dJson)
	#return JsonResponse({'drinks':[{'drink':{'title':'arnar','rating':2.5,'description':'lorem ipsum', 'location':'locationLocation'}},{'drink':{'title':'bob','rating':2.2,'description':'Testing description', 'location':'loc1,loc2'}}]})


# Create your views here.
def getDrinks(self):
	return getAllJSON()
	#return JsonResponse({'drinks':[{'drink':{'title':'arnar','rating':2.5,'description':'lorem ipsum', 'location':'locationLocation'}},{'drink':{'title':'bob','rating':2.2,'description':'Testing description', 'location':'loc1,loc2'}}]})
