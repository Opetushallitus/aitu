// Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
//
// This program is free software:  Licensed under the EUPL, Version 1.1 or - as
// soon as they will be approved by the European Commission - subsequent versions
// of the EUPL (the "Licence");
//
// You may not use this work except in compliance with the Licence.
// You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// European Union Public Licence for more details.

// Generated on 2013-10-24 using generator-angular 0.5.1
'use strict';

// # Globbing
// for performance reasons we're only matching one level down:
// 'test/spec/{,*/}*.js'
// use this if you want to recursively match all subfolders:
// 'test/spec/**/*.js'

module.exports = function (grunt) {
  require('load-grunt-tasks')(grunt);
  require('time-grunt')(grunt);

  grunt.initConfig({
    yeoman: {
      // configurable paths
      app: '../resources/public'
    },
    connect: {
      options: {
        port: 9000,
        // Change this to '0.0.0.0' to access the server from outside.
        hostname: 'localhost',
        livereload: 35729
      },
      test: {
        options: {
          port: 9001,
          base: [
            'test',
            '<%= yeoman.app %>'
          ]
        }
      }
    },
    karma: {
      unit: {
        configFile: 'karma.conf.js',
        autoWatch: false,
        singleRun: true
      },
      unit_ff: {
        configFile: 'karma.conf.js',
        autoWatch: false,
        singleRun: true,
        browsers: ['Firefox'],
        colors: false
      },
      unit_auto: {
        configFile: 'karma.conf.js'
      }
    },
    sass: {
      compile : {
        files: {
          '../resources/public/css/main.css': '../resources/private/sass/main.scss'
        }
      },
      dist : {
        files: {
          '../resources/public/css/main.css': '../resources/private/sass/main.scss'
        }
      },
      options : {
        outputStyle: 'expanded',
        sourceMap: true
      }
    },
    concat: {
      options : {
        process : false
      },
      dist : {
        src: ['bower_components/jquery/dist/jquery.min.js',
              'bower_components/angular/angular.min.js',
              'bower_components/angular-resource/angular-resource.min.js',
              'bower_components/angular-route/angular-route.min.js',
              'bower_components/angular-cookies/angular-cookies.min.js',
              'bower_components/select2/select2.min.js',
              'bower_components/angular-ui-select2/src/select2.js',
              'bower_components/jquery-waypoints/waypoints.min.js',
              'bower_components/lodash/lodash.min.js',
              'bower_components/stacktrace/stacktrace.js',
              'bower_components/jquery-placeholder/jquery.placeholder.min.js',
              'bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js',
              'bower_components/ngUpload/ng-upload.js',
              'bower_components/angular-loading-bar/build/loading-bar.min.js',
              'bower_components/aituaipaljs/src/js/yhteiset/palvelut/lokalisointi.js',
              'bower_components/aituaipaljs/src/js/yhteiset/palvelut/virheLogitus.js'],
        dest: '<%= yeoman.app %>/vendor/bower-components.js'
      }
    },
    copy: {
      test : {
        expand : true,
        cwd: 'bower_components',
        src : ['angular-mocks/angular-mocks.js'],
        dest : 'test/vendor',
        flatten : true
      },
      dist : {
        expand : true,
        cwd: 'bower_components',
        src : ['angular/angular.min.js.map',
               'angular-route/angular-route.min.js.map',
               'angular-resource/angular-resource.min.js.map',
               'angular-cookies/angular-cookies.min.js.map',
               'jquery/dist/jquery.min.map',
               'select2/*.{png,gif}',
               'select2/select2.css',
               'angular-loading-bar/build/loading-bar.css',
               'modernizr/modernizr.js'],
        dest: '<%= yeoman.app %>/vendor',
        flatten : true
      }
    },
    clean: {
      files : ['<%= yeoman.app %>/vendor',
               'test/vendor'],
      options: {force : true}
    },
    watch: {
      sass: {
        files: ['../resources/private/sass/**/*.scss'],
        tasks: ['sass:compile']
      }
    }
  });

  grunt.registerTask('bower', [
    'clean',
    'concat:dist',
    'copy:dist',
    'copy:test'
  ]);

  grunt.registerTask('build', [
    'bower',
    'sass:compile'
  ]);

  grunt.registerTask('test', [
    'connect:test',
    'karma:unit'
  ]);

  grunt.registerTask('test_ff', [
    'connect:test',
    'karma:unit_ff'
  ]);

  grunt.registerTask('autotest', [
    'connect:test',
    'karma:unit_auto'
  ]);

  grunt.registerTask('default', [
    'test'
  ]);
};
