# -*- coding: utf-8 -*-
# Generated by Django 1.11.5 on 2018-02-21 18:30
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='user',
            name='hashKey',
            field=models.CharField(default='', max_length=256, null=True),
        ),
    ]